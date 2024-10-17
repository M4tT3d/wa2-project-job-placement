package org.example.crm.integrationTest.testRestTemplate

import org.example.crm.dtos.request.create.CContactDTO
import org.example.crm.dtos.request.create.CCustomerDTO
import org.example.crm.dtos.request.create.CProfessionalDTO
import org.example.crm.dtos.request.update.UContactDTO
import org.example.crm.dtos.request.update.UCustomerDTO
import org.example.crm.dtos.request.update.UProfessionalDTO
import org.example.crm.integrationTest.controllers.IntegrationTest
import org.example.crm.utils.enums.Category
import org.example.crm.utils.enums.EmploymentState
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/sql/contactData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class ContactControllerTestRestTemplate : IntegrationTest() {
    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun getAllContacts() {
        val res: ResponseEntity<Map<String, Any>> =
            restTemplate.getForEntity("/api/contacts")
        assert(res.statusCode == HttpStatus.OK)
        assert(res.hasBody())
        println("Body: ${res.body}")
    }

    @Test
    fun getAllContactsWithPagination() {
        val res: ResponseEntity<Map<String, Any>> =
            restTemplate.getForEntity("/api/contacts?pageNumber=1&limit=1")
        assert(res.statusCode == HttpStatus.OK)
        assert(res.hasBody())
        assert((res.body?.get("contacts") as List<*>).size == 1)
        println("Body: ${res.body}")
    }

    @Test
    fun getAllContactsWithPaginationBadRequest() {
        val res: ResponseEntity<Map<String, Any>> =
            restTemplate.getForEntity("/api/contacts?pageNumber=1&limit=-1")
        assert(res.statusCode == HttpStatus.BAD_REQUEST)
        assert(res.hasBody())
        println("Body: ${res.body}")
    }

    @Test
    fun getAllProfessionals() {
        val res: ResponseEntity<Map<String, Any>> =
            restTemplate.getForEntity("/api/professionals")
        assert(res.statusCode == HttpStatus.OK)
        assert(res.hasBody())
        println("Body: ${res.body}")
    }

    @Test
    fun getAllCustomers() {
        val res: ResponseEntity<Map<String, Any>> =
            restTemplate.getForEntity("/api/customers")
        assert(res.statusCode == HttpStatus.OK)
        assert(res.hasBody())
        println("Body: ${res.body}")
    }

    @Test
    fun getContactById() {
        val res: ResponseEntity<Map<String, Any>> =
            restTemplate.getForEntity("/api/contacts/1")
        assert(res.statusCode == HttpStatus.OK)
        assert(res.hasBody())
        assert(res.body?.get("contact") != null)
        println("Body: ${res.body}")
    }

    @Test
    fun getContactByIdNotFound() {
        val res: ResponseEntity<Map<String, Any>> =
            restTemplate.getForEntity("/api/contacts/2")
        assert(res.statusCode == HttpStatus.NOT_FOUND)
        assert(res.hasBody())
        println("Body: ${res.body}")
    }

    @Test
    fun addContact() {
        val newContact = CContactDTO(
            name = "Mario",
            surname = "Rossi",
            category = Category.UNKNOWN,
        )
        val res: ResponseEntity<Map<String, Any>> =
            restTemplate.postForEntity(
                "/api/contacts", newContact
            )
        assert(res.statusCode == HttpStatus.CREATED)
        assert(res.hasBody())
        assert(res.body?.get("contact") != null)
        println("Body: ${res.body}")
    }

    @Test
    fun addContactBadRequest() {
        val newContact = CContactDTO(
            name = "Mario",
            surname = "Rossi",
            category = Category.PROFESSIONAL
        )
        val res: ResponseEntity<Map<String, Any>> =
            restTemplate.postForEntity(
                "/api/contacts", newContact
            )
        assert(res.statusCode == HttpStatus.BAD_REQUEST)
        assert(res.hasBody())
        println("Body: ${res.body}")
    }

    @Test
    fun addContactProfessional() {
        val newProfessional = CContactDTO(
            name = "Mario",
            surname = "Rossi",
            category = Category.PROFESSIONAL,
            professional = CProfessionalDTO(
                dailyRate = 100.0f,
                employmentState = EmploymentState.UNEMPLOYED_AVAILABLE,
                skills = setOf(1, 2)
            )
        )
        val res: ResponseEntity<Map<String, Any>> =
            restTemplate.postForEntity(
                "/api/contacts", newProfessional
            )
        assert(res.statusCode == HttpStatus.CREATED)
        assert(res.hasBody())
        assert(res.body?.get("contact") != null)
        println("Body: ${res.body}")
    }

    @Test
    fun addContactCustomer() {
        val newCustomer = CContactDTO(
            name = "Mario",
            surname = "Rossi",
            category = Category.CUSTOMER,
            customer = CCustomerDTO()
        )
        val res: ResponseEntity<Map<String, Any>> =
            restTemplate.postForEntity(
                "/api/contacts", newCustomer
            )
        assert(res.statusCode == HttpStatus.CREATED)
        assert(res.hasBody())
        println("Body: ${res.body}")
    }

    @Test
    fun addContactCustomerBadRequest() {
        val newCustomer = CContactDTO(
            name = "Mario",
            surname = "Rossi",
            category = Category.CUSTOMER,
        )
        val res: ResponseEntity<Map<String, Any>> =
            restTemplate.postForEntity(
                "/api/contacts", newCustomer
            )
        assert(res.statusCode == HttpStatus.BAD_REQUEST)
        assert(res.hasBody())
        println("Body: ${res.body}")
    }

    @Test
    fun updateContact() {
        val updatedContact = UContactDTO(
            name = "Ettore",
            surname = "Rossi",
            category = Category.CUSTOMER,
            customer = UCustomerDTO()
        )
        val res: ResponseEntity<Map<String, Any>> =
            restTemplate.exchange(
                "/api/contacts/1",
                HttpMethod.PATCH,
                HttpEntity(updatedContact),
                object : ParameterizedTypeReference<Map<String, Any>>() {}
            )
        assert(res.statusCode == HttpStatus.OK)
        assert(res.hasBody())
        assert(res.body?.get("contact") != null)
        assert((res.body?.get("contact") as Map<*, *>)["name"] == "Ettore")
        println("Body: ${res.body}")
    }

    @Test
    fun updateContactNotFound() {
        val updatedContact = UContactDTO(
            name = "Ettore",
            surname = "Rossi",
            category = Category.CUSTOMER,
            customer = UCustomerDTO()
        )
        val res: ResponseEntity<Map<String, Any>>? =
            restTemplate.exchange(
                "/api/contacts/2",
                HttpMethod.PATCH,
                HttpEntity(updatedContact),
                object : ParameterizedTypeReference<Map<String, Any>>() {}
            )
        assert(res?.statusCode == HttpStatus.NOT_FOUND)
        assert(res?.hasBody() == true)
        println("Body: ${res?.body}")
    }

    @Test
    fun updateContactBadRequest() {
        val updatedContact = UContactDTO(
            name = "Ettore",
            surname = "Rossi",
            category = Category.UNKNOWN
        )
        val res: ResponseEntity<Map<String, Any>>? =
            restTemplate.exchange(
                "/api/contacts/1",
                HttpMethod.PATCH,
                HttpEntity(updatedContact),
                object : ParameterizedTypeReference<Map<String, Any>>() {}
            )
        assert(res?.statusCode == HttpStatus.BAD_REQUEST)
        assert(res?.hasBody() == true)
        println("Body: ${res?.body}")
    }

    @Test
    fun updateProfessional() {
        val updatedContact = UContactDTO(
            name = "Ettore",
            surname = "Rossi",
            category = Category.PROFESSIONAL,
            professional = UProfessionalDTO(
                dailyRate = 100.0f,
                employmentState = EmploymentState.UNEMPLOYED_AVAILABLE,
                skills = setOf(1, 2)
            )
        )
        val res: ResponseEntity<Map<String, Any>> =
            restTemplate.exchange(
                "/api/contacts/101",
                HttpMethod.PATCH,
                HttpEntity(updatedContact),
                object : ParameterizedTypeReference<Map<String, Any>>() {}
            )
        assert(res.statusCode == HttpStatus.OK)
        assert(res.hasBody())
        assert(res.body?.get("contact") != null)
        assert((res.body?.get("contact") as Map<*, *>)["name"] == "Ettore")
        println("Body: ${res.body}")
    }

    @Test
    fun unknownContactToProfessional() {
        val updatedContact = UContactDTO(
            name = "Franco",
            surname = "Rossi",
            category = Category.PROFESSIONAL,
            professional = UProfessionalDTO(
                dailyRate = 100.0f,
                employmentState = EmploymentState.UNEMPLOYED_AVAILABLE,
                skills = setOf(1, 2)
            )
        )
        val res: ResponseEntity<Map<String, Any>> =
            restTemplate.exchange("/api/contacts/51", HttpMethod.PATCH, HttpEntity(updatedContact))
        assert(res.statusCode == HttpStatus.OK)
        assert(res.hasBody())
        assert(res.body?.get("contact") != null)
        assert((res.body?.get("contact") as Map<*, *>)["name"] == "Franco")
        assert((res.body?.get("contact") as Map<*, *>)["professional"] != null)
        println("Body: ${res.body}")
    }

    @Test
    fun deleteContact() {
        val res: ResponseEntity<Map<String, Any>>? =
            restTemplate.exchange(
                "/api/contacts/151",
                HttpMethod.DELETE,
                null,
                object : ParameterizedTypeReference<Map<String, Any>>() {}
            )
        assert(res?.statusCode == HttpStatus.NO_CONTENT)
        assert(res?.hasBody() == false)
    }

    @Test
    fun addEmailToContact() {
        val res: ResponseEntity<Map<String, Any>>? =
            restTemplate.exchange(
                "/api/contacts/1/email",
                HttpMethod.POST,
                HttpEntity(mapOf("email" to "mario@ee.cc")),
                object : ParameterizedTypeReference<Map<String, Any>>() {}
            )
        assert(res?.statusCode == HttpStatus.CREATED)
        assert(res?.hasBody() == true)
        assert(res?.body?.get("contact") != null)
        assert(((res?.body?.get("contact") as Map<*, *>)["emails"] as List<*>).size == 3)
        println("Body: ${res.body}")
    }

    @Test
    fun addEmailToContactBadRequest() {
        val res: ResponseEntity<Map<String, Any>>? =
            restTemplate.exchange(
                "/api/contacts/1/email",
                HttpMethod.POST,
                HttpEntity(mapOf("email" to "marioee.cc")),
                object : ParameterizedTypeReference<Map<String, Any>>() {}
            )
        assert(res?.statusCode == HttpStatus.BAD_REQUEST)
        assert(res?.hasBody() == true)
        println("Body: ${res?.body}")
    }

    @Test
    fun updateEmailToContact() {
        val res: ResponseEntity<Map<String, Any>>? =
            restTemplate.exchange(
                "/api/contacts/1/email/1",
                HttpMethod.PATCH,
                HttpEntity(mapOf("email" to "ettore@ee.com", "comment" to "test")),
                object : ParameterizedTypeReference<Map<String, Any>>() {}
            )
        assert(res?.statusCode == HttpStatus.OK)
        assert(res?.hasBody() == true)
        assert(res?.body?.get("contact") != null)
        assert((((res?.body?.get("contact") as Map<*, *>)["emails"] as List<*>)[0] as Map<*, *>)["email"] == "ettore@ee.com")
        println("Body: ${res.body}")
    }

    @Test
    fun deleteEmailToContact() {
        val res: ResponseEntity<Map<String, Any>>? =
            restTemplate.exchange(
                "/api/contacts/1/email/1",
                HttpMethod.DELETE,
                null,
                object : ParameterizedTypeReference<Map<String, Any>>() {}
            )
        assert(res?.statusCode == HttpStatus.NO_CONTENT)
        assert(res?.hasBody() == false)
    }
}