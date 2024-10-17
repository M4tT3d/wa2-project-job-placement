package org.example.crm.integrationTest.testRestTemplate

import org.example.crm.dtos.request.create.CJobOfferDTO
import org.example.crm.dtos.request.update.UStateJobOfferDTO
import org.example.crm.integrationTest.controllers.IntegrationTest
import org.example.crm.utils.enums.EmploymentState
import org.example.crm.utils.enums.StateJobOffer
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/sql/contactData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql("/sql/jobOfferData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class JobOfferControllerTestRestTemplate : IntegrationTest() {
    @Autowired
    lateinit var restTemplate: TestRestTemplate

    val jobOfferIdOne = mapOf(
        "id" to 1,
        "description" to "JobOffer 1",
        "status" to "CREATED",
        "comment" to "Comment 1",
        "customer id" to 1,
        "professional id" to null,
    )

    @Test
    fun getJobOfferById() {
        val res: ResponseEntity<Map<String, Any?>> =
            restTemplate.getForEntity("/api/joboffers/1")
        assert(res.statusCode == HttpStatus.OK)
        assert(res.body?.get("id") == jobOfferIdOne["id"])
        assert(res.body?.get("description") == jobOfferIdOne["description"])
        assert(res.body?.get("status") == jobOfferIdOne["status"])
        assert(res.body?.get("comment") == jobOfferIdOne["comment"])
        assert(res.body?.get("customer id") == jobOfferIdOne["customer id"])
        assert(res.body?.get("professional id") == jobOfferIdOne["professional id"])
    }

    @Test
    fun getJobOffersByIdError() {
        val res: ResponseEntity<Map<String, Any?>> =
            restTemplate.getForEntity("/api/joboffers/2")
        assert(res.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun getListAllWithPaginationAndLimit() {
        val res: ResponseEntity<List<Map<String, Any?>>> =
            restTemplate.getForEntity("/api/joboffers/?pageNumber=1&limit=1")
        assert(res.statusCode == HttpStatus.OK)
        assert(res.body?.get(0)?.get("id") == jobOfferIdOne["id"])
        assert(res.body?.get(0)?.get("description") == jobOfferIdOne["description"])
        assert(res.body?.get(0)?.get("status") == jobOfferIdOne["status"])
        assert(res.body?.get(0)?.get("comment") == jobOfferIdOne["comment"])
        assert(res.body?.get(0)?.get("customer id") == jobOfferIdOne["customer id"])
        assert(res.body?.get(0)?.get("professional id") == jobOfferIdOne["professional id"])
    }

    @Test
    fun getListAllWithOpen() {
        val jobOffers = listOf(
            mapOf("id" to 1),
            mapOf("id" to 51),
            mapOf("id" to 101),
            mapOf("id" to 151),
        )
        val res: ResponseEntity<List<Map<String, Any?>>> =
            restTemplate.getForEntity("/api/joboffers/?customerId=1&jobOffer=open")
        assert(res.statusCode == HttpStatus.OK)
        assert(res.body?.get(0)?.get("id") == jobOffers[0]["id"])
        assert(res.body?.get(1)?.get("id") == jobOffers[1]["id"])
        assert(res.body?.get(2)?.get("id") == jobOffers[2]["id"])
        assert(res.body?.get(3)?.get("id") == jobOffers[3]["id"])
    }

    @Test
    fun getListAllWithOpenWithoutCustomerIdParam() {
        val res: ResponseEntity<Map<String, Any?>> =
            restTemplate.getForEntity("/api/joboffers/?jobOffer=open")
        assert(res.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun getListAllWithAccepted() {
        val jobOffers = mapOf("id" to 451)
        val res: ResponseEntity<List<Map<String, Any?>>> =
            restTemplate.getForEntity("/api/joboffers/?professionalId=51&jobOffer=accepted")
        assert(res.statusCode == HttpStatus.OK)
        assert(res.body?.get(0)?.get("id") == jobOffers["id"])
    }

    @Test
    fun getListAllWithAborted() {
        val jobOffer = mapOf("id" to 351)
        val res: ResponseEntity<List<Map<String, Any?>>> =
            restTemplate.getForEntity("/api/joboffers/?jobOffer=aborted")
        assert(res.statusCode == HttpStatus.OK)
        assert(res.body?.get(0)?.get("id") == jobOffer["id"])
    }

    @Test
    fun getListAllWithSpecificState() {
        val jobOffer = listOf(
            mapOf("Id" to 151),
            mapOf("Id" to 301),
            mapOf("Id" to 401),
        )
        val res: ResponseEntity<List<Map<String, Any?>>> =
            restTemplate.getForEntity("/api/joboffers/?jobOffer=CANDIDATE_PROPOSAL")
        assert(res.statusCode == HttpStatus.OK)
        assert(res.body?.get(0)?.get("id") == jobOffer[0]["Id"])
        assert(res.body?.get(1)?.get("id") == jobOffer[1]["Id"])
        assert(res.body?.get(2)?.get("id") == jobOffer[2]["Id"])
    }

    @Test
    fun getListAllWithSpecificStateAndCustomerId() {
        val jobOffer = listOf(
            mapOf("Id" to 1),
            mapOf("Id" to 51),
        )
        val res: ResponseEntity<List<Map<String, Any?>>> =
            restTemplate.getForEntity("/api/joboffers/?customerId=1&jobOffer=CREATED")
        assert(res.statusCode == HttpStatus.OK)
        assert(res.body?.get(0)?.get("id") == jobOffer[0]["Id"])
        assert(res.body?.get(1)?.get("id") == jobOffer[1]["Id"])
    }

    @Test
    fun getListAllWithSpecificStateAndProfessionalId() {
        val jobOffer = mapOf("Id" to 401)
        val res: ResponseEntity<List<Map<String, Any?>>> =
            restTemplate.getForEntity("/api/joboffers/?professionalId=51&jobOffer=CANDIDATE_PROPOSAL")
        assert(res.statusCode == HttpStatus.OK)
        assert(res.body?.get(0)?.get("id") == jobOffer["Id"])
    }

    @Test
    fun getListAllWithSpecificStateAndProfessionalIdNotExist() {
        val res: ResponseEntity<List<Map<String, Any?>>> =
            restTemplate.getForEntity("/api/joboffers/?professionalId=425&jobOffer=CANDIDATE_PROPOSAL")
        assert(res.statusCode == HttpStatus.OK)
        assert(res.body?.size == 0)
    }

    @Test
    fun getLissAllWithFilterSkills() {
        val jobOffer = mapOf("id" to 1)
        val res: ResponseEntity<List<Map<String, Any?>>> =
            restTemplate.getForEntity("/api/joboffers/?skills=1,2")
        assert(res.statusCode == HttpStatus.OK)
        assert(res.body?.get(0)?.get("id") == jobOffer["id"])
    }

    @Test
    fun getListAllWithFilterSkillsAndProfessionalId() {
        val jobOffer = mapOf("id" to 401)
        val res: ResponseEntity<List<Map<String, Any?>>> =
            restTemplate.getForEntity("/api/joboffers/?professionalId=51&skills=2,3")
        assert(res.statusCode == HttpStatus.OK)
        assert(res.body?.get(0)?.get("id") == jobOffer["id"])
    }

    @Test
    fun insertJobOffer() {
        val newJobOffer = CJobOfferDTO(
            description = "pasticciere",
            skills = mutableSetOf(1, 2),
            comment = "asf",
            duration = 2f,
            customerId = 1
        )
        val res: ResponseEntity<Map<String, Any?>> =
            restTemplate.postForEntity("/api/joboffers", newJobOffer)
        assert(res.statusCode == HttpStatus.CREATED)
        // assert(res.body?.get("id") == 52)
        assert(res.body?.get("description") == newJobOffer.description)
        assert(res.body?.get("status") == StateJobOffer.CREATED.toString())

    }

    @Test
    fun updateJobOffer() {
        val newJobOffer = CJobOfferDTO(
            description = "pasticciere",
            skills = mutableSetOf(1, 2),
            comment = "asf",
            duration = 2f,
            customerId = 1
        )
        val res1: ResponseEntity<Map<String, Any?>> =
            restTemplate.postForEntity("/api/joboffers", newJobOffer)

        val updateJobOffer = CJobOfferDTO(
            description = "giardiniere ",
            skills = mutableSetOf(1, 2),
            comment = "A torino",
            duration = 4f,
            customerId = 1
        )

        val requestEntity = HttpEntity(updateJobOffer, HttpHeaders())
        val res: ResponseEntity<Map<String, Any?>> =
            restTemplate.exchange(
                "/api/joboffers/${res1.body?.get("id")}",
                org.springframework.http.HttpMethod.PUT,
                requestEntity,
                object : ParameterizedTypeReference<Map<String, Any?>>() {}
            )

        assert(res.statusCode == HttpStatus.OK)
        assert(res.body?.get("description") == "giardiniere ")
        assert(res.body?.get("comment") == "A torino")
        assert(res.body?.get("duration") == 4.0)
        assert(res.body?.get("status") == res1.body?.get("status"))
    }

    @Test
    fun updateJobOfferNoJobOffer() {
        val updateJobOffer = CJobOfferDTO(
            description = "giardiniere ",
            skills = mutableSetOf(1, 2),
            comment = "A torino",
            duration = 4f,
            customerId = 1
        )

        val requestEntity = HttpEntity(updateJobOffer, HttpHeaders())
        val res: ResponseEntity<Map<String, Any?>> =
            restTemplate.exchange(
                "/api/joboffers/105",
                org.springframework.http.HttpMethod.PUT,
                requestEntity,
                object : ParameterizedTypeReference<Map<String, Any?>>() {}
            )

        assert(res.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun updateStateJobOffer() {
        val newJobOffer = CJobOfferDTO(
            description = "pasticciere",
            skills = mutableSetOf(1, 2),
            comment = "asf",
            duration = 2f,
            customerId = 1
        )
        val res1: ResponseEntity<Map<String, Any?>> =
            restTemplate.postForEntity("/api/joboffers", newJobOffer)

        var updateStateJobOffer = UStateJobOfferDTO(
            status = StateJobOffer.SELECTION_PHASE
        )
        var res: ResponseEntity<Map<String, Any?>> =
            restTemplate.postForEntity("/api/joboffers/${res1.body?.get("id")}", updateStateJobOffer)
        assert(res.statusCode == HttpStatus.OK)
        assert(res.body?.get("status") == updateStateJobOffer.status.toString())

        updateStateJobOffer = UStateJobOfferDTO(
            status = StateJobOffer.CANDIDATE_PROPOSAL,
            professionalId = 1
        )
        res = restTemplate.postForEntity("/api/joboffers/${res1.body?.get("id")}", updateStateJobOffer)
        var resContact = restTemplate.getForEntity<Map<String, Any?>>("/api/contacts/101")

        var employmentState =
            ((resContact.body?.get("contact") as Map<*, *>)["professional"] as Map<*, *>)["employmentState"]
        val idProfessional = ((resContact.body?.get("contact") as Map<*, *>)["professional"] as Map<*, *>)["id"]
        assert(res.body?.get("status") == updateStateJobOffer.status.toString())
        assert(res.body?.get("professional id") == idProfessional)
        assert(employmentState == EmploymentState.UNEMPLOYED_AVAILABLE.toString())

        updateStateJobOffer = UStateJobOfferDTO(
            status = StateJobOffer.CONSOLIDATED,
            professionalId = 1
        )
        res = restTemplate.postForEntity("/api/joboffers/${res1.body?.get("id")}", updateStateJobOffer)
        resContact = restTemplate.getForEntity<Map<String, Any?>>("/api/contacts/101")

        employmentState =
            ((resContact.body?.get("contact") as Map<*, *>)["professional"] as Map<*, *>)["employmentState"]

        assert(res.body?.get("status") == updateStateJobOffer.status.toString())
        assert(res.body?.get("professional id") == idProfessional)
        assert(employmentState == EmploymentState.EMPLOYED.toString())

        updateStateJobOffer = UStateJobOfferDTO(
            status = StateJobOffer.DONE,
            professionalId = 1
        )
        res = restTemplate.postForEntity("/api/joboffers/${res1.body?.get("id")}", updateStateJobOffer)
        resContact = restTemplate.getForEntity<Map<String, Any?>>("/api/contacts/101")
        employmentState =
            ((resContact.body?.get("contact") as Map<*, *>)["professional"] as Map<*, *>)["employmentState"]

        assert(res.body?.get("status") == updateStateJobOffer.status.toString())
        assert(res.body?.get("professional id") == idProfessional)
        assert(employmentState == EmploymentState.UNEMPLOYED_AVAILABLE.toString())
    }

    @Test
    fun updateStateJobOfferWrongState() {
        val newJobOffer = CJobOfferDTO(
            description = "pasticciere",
            skills = mutableSetOf(1, 2),
            comment = "asf",
            duration = 2f,
            customerId = 1
        )
        val res1: ResponseEntity<Map<String, Any?>> =
            restTemplate.postForEntity("/api/joboffers", newJobOffer)

        val updateStateJobOffer = UStateJobOfferDTO(
            status = StateJobOffer.CANDIDATE_PROPOSAL,
        )
        val res: ResponseEntity<Map<String, Any?>> =
            restTemplate.postForEntity("/api/joboffers/${res1.body?.get("id")}", updateStateJobOffer)
        assert(res.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateStateJobOfferNoProfessionalId() {
        val updateStateJobOffer = UStateJobOfferDTO(
            status = StateJobOffer.CANDIDATE_PROPOSAL,
        )
        val res: ResponseEntity<Map<String, Any?>> =
            restTemplate.postForEntity("/api/joboffers/101", updateStateJobOffer)
        assert(res.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateStateJobOfferNoJobOffer() {
        val updateStateJobOffer = UStateJobOfferDTO(
            status = StateJobOffer.CANDIDATE_PROPOSAL,
            professionalId = 1
        )
        val res: ResponseEntity<Map<String, Any?>> =
            restTemplate.postForEntity("/api/joboffers/105", updateStateJobOffer)
        assert(res.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun updateStateJobOfferAbortedNoProfessionalId() {
        val updateStateJobOffer = UStateJobOfferDTO(
            status = StateJobOffer.ABORTED,
        )
        val res: ResponseEntity<Map<String, Any?>> =
            restTemplate.postForEntity("/api/joboffers/201", updateStateJobOffer)

        assert(res.statusCode == HttpStatus.BAD_REQUEST)


    }

    @Test
    fun getValue() {
        val res: ResponseEntity<Map<String, Any?>> =
            restTemplate.getForEntity("/api/joboffers/151/value")
        assert(res.statusCode == HttpStatus.OK)
        assert(res.body?.get("value") == 100.0)
    }

    @Test
    fun getValueNoJobOffer() {
        val res: ResponseEntity<Map<String, Any?>> =
            restTemplate.getForEntity("/api/joboffers/105/value")
        assert(res.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun getValueNoProfessional() {
        val res: ResponseEntity<Map<String, Any?>> =
            restTemplate.getForEntity("/api/joboffers/1/value")
        assert(res.body?.get("error") == "No professional associated with this job offer")
        assert(res.statusCode == HttpStatus.BAD_REQUEST)

    }

}