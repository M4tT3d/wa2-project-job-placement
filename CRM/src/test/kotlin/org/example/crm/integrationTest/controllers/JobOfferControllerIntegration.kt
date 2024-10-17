package org.example.crm.integrationTest.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.example.crm.dtos.request.create.CJobOfferDTO
import org.example.crm.dtos.request.update.UStateJobOfferDTO
import org.example.crm.utils.enums.EmploymentState
import org.example.crm.utils.enums.StateJobOffer
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath


@AutoConfigureMockMvc
@Sql("/sql/skillData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql("/sql/contactData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql("/sql/jobOfferData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class JobOfferControllerIntegrationTest : IntegrationTest() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper


    val jobOfferIdOne = mapOf(
        "id" to 1,
        "description" to "JobOffer 1",
        "status" to "CREATED",
        "comment" to "Comment 1",
        "customer id" to 1,
        "professional id" to null,
    )


    @Test
    fun `create job offer`() {
        val request = CJobOfferDTO(
            description = "Job Offer 1",
            skills = setOf(1, 2),
            comment = "Comment",
            duration = 10f,
            customerId = 1L
        )

        val jobOfferJson = objectMapper.writeValueAsString(request)
        mockMvc.post("/api/joboffers") {
            contentType = MediaType.APPLICATION_JSON
            content = jobOfferJson
        }.andExpect {
            status { isCreated() }
            jsonPath("$.id").isNumber
            jsonPath("$.description").value("Job Offer 1")
            jsonPath("$.status").value("CREATED")
            jsonPath("$.skills").isArray
            jsonPath("$.comment").value("Comment")
            jsonPath("$.duration").value(10)
        }

    }

    @Test
    fun getJobOfferById() {
        mockMvc.get("/api/joboffers/1").andExpect {
            status { isOk() }
            content { jsonPath("$.id", Matchers.`is`(jobOfferIdOne["id"])) }
            content { jsonPath("$.description", Matchers.`is`(jobOfferIdOne["description"])) }
            content { jsonPath("$.status", Matchers.`is`(jobOfferIdOne["status"])) }
            content { jsonPath("$.comment", Matchers.`is`(jobOfferIdOne["comment"])) }
            //content { jsonPath("$.customer id", Matchers.`is`(jobOfferIdOne["customer id"])) }
            //content { jsonPath("$.professional id", Matchers.`is`(jobOfferIdOne["professional id"])) }
        }
    }

    @Test
    fun getJobOffersByIdError() {
        mockMvc.get("/api/joboffers/2").andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun getListAllWithPaginationAndLimit() {
        mockMvc.get("/api/joboffers/?pageNumber=1&limit=1").andExpect {
            status { isOk() }
            content { jsonPath("$[0].id", Matchers.`is`(jobOfferIdOne["id"])) }
            content { jsonPath("$[0].description", Matchers.`is`(jobOfferIdOne["description"])) }
            content { jsonPath("$[0].status", Matchers.`is`(jobOfferIdOne["status"])) }
            content { jsonPath("$[0].comment", Matchers.`is`(jobOfferIdOne["comment"])) }
            //content { jsonPath("$[0].customer id", Matchers.`is`(jobOfferIdOne["customer id"])) }
            //content { jsonPath("$[0].professional id", Matchers.`is`(jobOfferIdOne["professional id"])) }
        }
    }

    @Test
    fun getListAllWithOpen() {
        val jobOffers = listOf(
            mapOf("id" to 1),
            mapOf("id" to 51),
            mapOf("id" to 101),
            mapOf("id" to 151),
        )
        mockMvc.get("/api/joboffers/?customerId=1&jobOffer=open").andExpect {
            status { isOk() }
            content { jsonPath("$[0].id", Matchers.`is`(jobOffers[0]["id"])) }
            content { jsonPath("$[1].id", Matchers.`is`(jobOffers[1]["id"])) }
            content { jsonPath("$[2].id", Matchers.`is`(jobOffers[2]["id"])) }
            content { jsonPath("$[3].id", Matchers.`is`(jobOffers[3]["id"])) }
        }
    }

    @Test
    fun getListAllWithOpenWithoutCustomerIdParam() {
        mockMvc.get("/api/joboffers/?jobOffer=open").andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun getListAllWithAccepted() {
        val jobOffers = mapOf("id" to 451)
        mockMvc.get("/api/joboffers/?professionalId=51&jobOffer=accepted").andExpect {
            status { isOk() }
            content { jsonPath("$[0].id", Matchers.`is`(jobOffers["id"])) }
        }
    }

    @Test
    fun getListAllWithAborted() {
        val jobOffer = mapOf("id" to 351)
        mockMvc.get("/api/joboffers/?jobOffer=aborted").andExpect {
            status { isOk() }
            content { jsonPath("$[0].id", Matchers.`is`(jobOffer["id"])) }
        }
    }

    @Test
    fun getListAllWithSpecificState() {
        val jobOffer = listOf(
            mapOf("Id" to 151),
            mapOf("Id" to 301),
            mapOf("Id" to 401),
        )
        mockMvc.get("/api/joboffers/?jobOffer=CANDIDATE_PROPOSAL").andExpect {
            status { isOk() }
            content { jsonPath("$[0].id", Matchers.`is`(jobOffer[0]["Id"])) }
            content { jsonPath("$[1].id", Matchers.`is`(jobOffer[1]["Id"])) }
            content { jsonPath("$[2].id", Matchers.`is`(jobOffer[2]["Id"])) }
        }
    }

    @Test
    fun getListAllWithSpecificStateAndCustomerId() {
        val jobOffer = listOf(
            mapOf("Id" to 1),
            mapOf("Id" to 51),
        )
        mockMvc.get("/api/joboffers/?customerId=1&jobOffer=CREATED").andExpect {
            status { isOk() }
            content { jsonPath("$[0].id", Matchers.`is`(jobOffer[0]["Id"])) }
            content { jsonPath("$[1].id", Matchers.`is`(jobOffer[1]["Id"])) }
        }
    }

    @Test
    fun getListAllWithSpecificStateAndProfessionalId() {
        val jobOffer = mapOf("Id" to 401)
        mockMvc.get("/api/joboffers/?professionalId=51&jobOffer=CANDIDATE_PROPOSAL").andExpect {
            status { isOk() }
            content { jsonPath("$[0].id", Matchers.`is`(jobOffer["Id"])) }
        }
    }

    @Test
    fun getListAllWithSpecificStateAndProfessionalIdNotExist() {
        mockMvc.get("/api/joboffers/?professionalId=425&jobOffer=CANDIDATE_PROPOSAL").andExpect {
            status { isOk() }
            content { jsonPath("$", Matchers.hasSize<Int>(0)) }
        }
    }

    @Test
    fun getLissAllWithFilterSkills() {
        val jobOffer = mapOf("id" to 1)
        mockMvc.get("/api/joboffers/?skills=1,2").andExpect {
            status { isOk() }
            content { jsonPath("$[0].id", Matchers.`is`(jobOffer["id"])) }
        }
    }

    @Test
    fun getListAllWithFilterSkillsAndProfessionalId() {
        val jobOffer = mapOf("id" to 401)
        mockMvc.get("/api/joboffers/?professionalId=51&skills=2,3").andExpect {
            status { isOk() }
            content { jsonPath("$[0].id", Matchers.`is`(jobOffer["id"])) }
        }
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

        val res1 = mockMvc.post("/api/joboffers") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(newJobOffer)
        }.andExpect {
            status { isCreated() }
        }.andReturn().response.contentAsString

        val jsonMapper = jacksonObjectMapper()
        val result = jsonMapper.readValue<Map<String, Any>>(res1).get("id").toString()

        val updateJobOffer = CJobOfferDTO(
            description = "giardiniere ",
            skills = mutableSetOf(1, 2),
            comment = "A torino",
            duration = 4f,
            customerId = 1
        )

        mockMvc.put("/api/joboffers/${result}") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateJobOffer)
        }.andExpect {
            status { isOk() }
            jsonPath("$.description", Matchers.`is`("giardiniere "))
            jsonPath("$.comment", Matchers.`is`("A torino"))
            jsonPath("$.duration", Matchers.`is`(4.0))
            jsonPath("$.status", Matchers.`is`("CREATED"))
        }
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

        mockMvc.put("/api/joboffers/105") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateJobOffer)
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun updateStateJobOffer() {
        val jsonMapper = jacksonObjectMapper()
        val newJobOffer = CJobOfferDTO(
            description = "pasticciere",
            skills = mutableSetOf(1, 2),
            comment = "asf",
            duration = 2f,
            customerId = 1
        )

        val res1 = mockMvc.post("/api/joboffers") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(newJobOffer)
        }.andExpect {
            status { isCreated() }
        }.andReturn().response.contentAsString
        val res1Id = jsonMapper.readValue<Map<String, Any>>(res1).get("id").toString()
        var updateStateJobOffer = UStateJobOfferDTO(
            status = StateJobOffer.SELECTION_PHASE
        )
        mockMvc.post("/api/joboffers/${res1Id}") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateStateJobOffer)
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.status", Matchers.`is`(updateStateJobOffer.status.toString())) }
        }


        updateStateJobOffer = UStateJobOfferDTO(
            status = StateJobOffer.CANDIDATE_PROPOSAL,
            professionalId = 1
        )
        var res = mockMvc.post("/api/joboffers/${res1Id}") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateStateJobOffer)
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.status", Matchers.`is`(updateStateJobOffer.status.toString())) }
        }.andReturn().response.contentAsString
        var resJson = jsonMapper.readValue<Map<String, Any>>(res)
        var resContact = mockMvc.get("/api/contacts/101").andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        var resContactJson = jsonMapper.readValue<Map<String, Any>>(resContact)
        var employmentState = ((resContactJson["contact"] as Map<*, *>)["professional"] as Map<*, *>)["employmentState"]
        var idProfessional = ((resContactJson["contact"] as Map<*, *>)["professional"] as Map<*, *>)["id"]
        assert(resJson["status"] == updateStateJobOffer.status.toString())
        assert(resJson["professional id"] == idProfessional)
        assert(employmentState == EmploymentState.UNEMPLOYED_AVAILABLE.toString())



        updateStateJobOffer = UStateJobOfferDTO(
            status = StateJobOffer.CONSOLIDATED,
            professionalId = 1
        )
        res = mockMvc.post("/api/joboffers/${res1Id}") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateStateJobOffer)
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.status", Matchers.`is`(updateStateJobOffer.status.toString())) }
        }.andReturn().response.contentAsString
        resJson = jsonMapper.readValue<Map<String, Any>>(res)
        resContact = mockMvc.get("/api/contacts/101").andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        resContactJson = jsonMapper.readValue<Map<String, Any>>(resContact)
        employmentState = ((resContactJson["contact"] as Map<*, *>)["professional"] as Map<*, *>)["employmentState"]
        idProfessional = ((resContactJson["contact"] as Map<*, *>)["professional"] as Map<*, *>)["id"]

        assert(resJson["status"] == updateStateJobOffer.status.toString())
        assert(resJson["professional id"] == idProfessional)
        assert(employmentState == EmploymentState.EMPLOYED.toString())


        updateStateJobOffer = UStateJobOfferDTO(
            status = StateJobOffer.DONE,
            professionalId = 1
        )
        res = mockMvc.post("/api/joboffers/${res1Id}") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateStateJobOffer)
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.status", Matchers.`is`(updateStateJobOffer.status.toString())) }
        }.andReturn().response.contentAsString
        resJson = jsonMapper.readValue<Map<String, Any>>(res)
        resContact = mockMvc.get("/api/contacts/101").andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString
        resContactJson = jsonMapper.readValue<Map<String, Any>>(resContact)
        employmentState = ((resContactJson["contact"] as Map<*, *>)["professional"] as Map<*, *>)["employmentState"]
        idProfessional = ((resContactJson["contact"] as Map<*, *>)["professional"] as Map<*, *>)["id"]

        assert(resJson["status"] == updateStateJobOffer.status.toString())
        assert(resJson["professional id"] == idProfessional)
        assert(employmentState == EmploymentState.UNEMPLOYED_AVAILABLE.toString())
    }

    @Test
    fun updateStateJobOfferWrongState() {
        val jsonMapper = jacksonObjectMapper()
        val newJobOffer = CJobOfferDTO(
            description = "pasticciere",
            skills = mutableSetOf(1, 2),
            comment = "asf",
            duration = 2f,
            customerId = 1
        )
        val res1 = mockMvc.post("/api/joboffers") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(newJobOffer)
        }.andExpect {
            status { isCreated() }
        }.andReturn().response.contentAsString
        val res1Id = jsonMapper.readValue<Map<String, Any>>(res1).get("id").toString()

        val updateStateJobOffer = UStateJobOfferDTO(
            status = StateJobOffer.CANDIDATE_PROPOSAL,
        )

        mockMvc.post("/api/joboffers/${res1Id}") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateStateJobOffer)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun updateStateJobOfferNoProfessionalId() {
        val updateStateJobOffer = UStateJobOfferDTO(
            status = StateJobOffer.CANDIDATE_PROPOSAL,
        )
        mockMvc.post("/api/joboffers/101") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateStateJobOffer)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun updateStateJobOfferNoJobOffer() {
        val updateStateJobOffer = UStateJobOfferDTO(
            status = StateJobOffer.CANDIDATE_PROPOSAL,
            professionalId = 1
        )

        mockMvc.post("/api/joboffers/105") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateStateJobOffer)
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun updateStateJobOfferAbortedNoProfessionalId() {
        val updateStateJobOffer = UStateJobOfferDTO(
            status = StateJobOffer.ABORTED,
        )

        mockMvc.post("/api/joboffers/201") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateStateJobOffer)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun getValue() {
        mockMvc.get("/api/joboffers/151/value").andExpect {
            status { isOk() }
            content { jsonPath("$.value", Matchers.`is`(100.0)) }
        }
    }

    @Test
    fun getValueNoJobOffer() {
        mockMvc.get("/api/joboffers/105/value").andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun getValueNoProfessional() {
        mockMvc.get("/api/joboffers/1/value").andExpect {
            status { isBadRequest() }
            content { jsonPath("$.error", Matchers.`is`("No professional associated with this job offer")) }
        }
    }
}

