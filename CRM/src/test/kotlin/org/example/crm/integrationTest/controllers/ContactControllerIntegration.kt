package org.example.crm.integrationTest.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.crm.dtos.request.create.CContactDTO
import org.example.crm.dtos.request.create.CCustomerDTO
import org.example.crm.dtos.request.create.CEmailDTO
import org.example.crm.dtos.request.create.CProfessionalDTO
import org.example.crm.entities.Address
import org.example.crm.entities.Email
import org.example.crm.entities.Telephone
import org.example.crm.utils.enums.Category
import org.example.crm.utils.enums.EmploymentState
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.*

@SpringBootTest
@AutoConfigureMockMvc
@Sql("/sql/skillData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql("/sql/contactData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class ContactControllerIntegrationTest : IntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `addContact should return status CREATED`() {
        val contactDTO = CContactDTO(
            name = "John",
            surname = "Doe",
            category = Category.UNKNOWN,
            comment = "This is a comment",
            emails = null,
            addresses = null,
            telephones = null,
            customer = null,
            ssnCode = null,
            professional = null
        )
        val contactJson = objectMapper.writeValueAsString(contactDTO)

        // Act and Assert
        mockMvc.post("/api/contacts") {
            contentType = MediaType.APPLICATION_JSON
            content = contactJson
        }.andExpect {
            status { isCreated() }
            jsonPath("$.contact", Matchers.notNullValue())
            jsonPath("$.contact.name", Matchers.`is`(contactDTO.name))
            jsonPath("$.contact.surname", Matchers.`is`(contactDTO.surname))
            jsonPath("$.contact.category", Matchers.`is`(contactDTO.category.name))
            jsonPath("$.contact.comment", Matchers.`is`(contactDTO.comment))
            jsonPath("$.contact.emails", Matchers.`is`(listOf<Email>()))
            jsonPath("$.contact.telephones", Matchers.`is`(listOf<Telephone>()))
            jsonPath("$.contact.addresses", Matchers.`is`(listOf<Address>()))
        }
    }

    @Test
    fun `addContact should return status CREATED with customer`() {
        val contactDTO = CContactDTO(
            name = "John",
            surname = "Doe",
            category = Category.CUSTOMER,
            comment = "This is a comment",
            emails = null,
            addresses = null,
            telephones = null,
            customer = CCustomerDTO(),
            ssnCode = null,
            professional = null
        )
        val contactJson = objectMapper.writeValueAsString(contactDTO)

        // Act and Assert
        mockMvc.post("/api/contacts") {
            contentType = MediaType.APPLICATION_JSON
            content = contactJson
        }.andExpect {
            status { isCreated() }
            jsonPath("$.contact", Matchers.notNullValue())
            jsonPath("$.contact.name", Matchers.`is`(contactDTO.name))
            jsonPath("$.contact.surname", Matchers.`is`(contactDTO.surname))
            jsonPath("$.contact.category", Matchers.`is`(contactDTO.category.name))
            jsonPath("$.contact.comment", Matchers.`is`(contactDTO.comment))
            jsonPath("$.contact.emails", Matchers.`is`(listOf<Email>()))
            jsonPath("$.contact.telephones", Matchers.`is`(listOf<Telephone>()))
            jsonPath("$.contact.addresses", Matchers.`is`(listOf<Address>()))
            jsonPath("$.contact.customer", Matchers.notNullValue())
        }
    }

    @Test
    fun `addContact should return BAD_REQUEST with customer`() {
        val contactDTO = CContactDTO(
            name = "John",
            surname = "Doe",
            category = Category.CUSTOMER,
            comment = "This is a comment",
            emails = null,
            addresses = null,
            telephones = null,
            customer = null,
            ssnCode = null,
            professional = null
        )
        val contactJson = objectMapper.writeValueAsString(contactDTO)

        // Act and Assert
        mockMvc.post("/api/contacts") {
            contentType = MediaType.APPLICATION_JSON
            content = contactJson
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.message", Matchers.`is`("Validation error"))
            jsonPath("$.statusCode", Matchers.`is`(400))
            jsonPath("$.description", Matchers.`is`("Customer data cannot be null"))
        }
    }

    @Test
    fun `addContact should return status CREATED with professional`() {
        val contactDTO = CContactDTO(
            name = "John",
            surname = "Doe",
            category = Category.PROFESSIONAL,
            comment = "This is a comment",
            emails = null,
            addresses = null,
            telephones = null,
            customer = null,
            professional = CProfessionalDTO(
                skills = mutableSetOf(1, 2),
                dailyRate = 100.0f,
                employmentState = EmploymentState.UNEMPLOYED_AVAILABLE,
                location = "Rome"
            ),
        )
        val contactJson = objectMapper.writeValueAsString(contactDTO)

        // Act and Assert
        mockMvc.post("/api/contacts") {
            contentType = MediaType.APPLICATION_JSON
            content = contactJson
        }.andExpect {
            status { isCreated() }
            jsonPath("$.contact", Matchers.notNullValue())
            jsonPath("$.contact.name", Matchers.`is`(contactDTO.name))
            jsonPath("$.contact.surname", Matchers.`is`(contactDTO.surname))
            jsonPath("$.contact.category", Matchers.`is`(contactDTO.category.name))
            jsonPath("$.contact.comment", Matchers.`is`(contactDTO.comment))
            jsonPath("$.contact.emails", Matchers.`is`(listOf<Email>()))
            jsonPath("$.contact.telephones", Matchers.`is`(listOf<Telephone>()))
            jsonPath("$.contact.addresses", Matchers.`is`(listOf<Address>()))
            jsonPath("$.contact.professional", Matchers.notNullValue())
        }
    }

    @Test
    fun `addContact should return status BAD_REQUEST`() {
        val contactDTO = mapOf(
            "name" to "John",
            "surname" to "",
            "category" to Category.UNKNOWN,
            "comment" to "This is a comment",
        )
        val contactJson = objectMapper.writeValueAsString(contactDTO)

        // Act and Assert
        mockMvc.post("/api/contacts") {
            contentType = MediaType.APPLICATION_JSON
            content = contactJson
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.message", Matchers.`is`("Validation error"))
            jsonPath("$.statusCode", Matchers.`is`(400))
            jsonPath("$.description", Matchers.`is`("Surname cannot be empty"))
        }
    }

    @Test
    fun `getAll should return status OK`() {
        // Act and Assert
        mockMvc.get("/api/contacts") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.contacts", Matchers.notNullValue())
        }
    }

    @Test
    fun `getById should return status OK`() {
        // Arrange
        val id = 1L

        // Act and Assert
        mockMvc.get("/api/contacts/$id") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.contact", Matchers.notNullValue())
            jsonPath("$.contact.id", Matchers.`is`(id.toInt()))
        }
    }

    @Test
    fun `getById should return status NOT_FOUND`() {
        // Arrange
        val id = 2L

        // Act and Assert
        mockMvc.get("/api/contacts/$id") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.message", Matchers.`is`("Entity not found"))
            jsonPath("$.statusCode", Matchers.`is`(404))
            jsonPath("$.description", Matchers.`is`("Contact not found"))
        }
    }

    @Test
    fun `deleteById should return status NO_CONTENT`() {
        // Arrange
        val id = 151L

        // Act and Assert
        mockMvc.request(HttpMethod.DELETE, "/api/contacts/$id") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNoContent() }
        }
    }

    @Test
    fun `update contact should return status OK`() {
        // Arrange
        val id = 51L
        val contactDTO = CContactDTO(
            name = "Mario",
            surname = "Rossi",
            category = Category.CUSTOMER,
            comment = "This is a comment",
            emails = listOf(CEmailDTO("mario.rossi@test.it")),
            addresses = null,
            telephones = null,
            customer = CCustomerDTO(),
            ssnCode = null,
            professional = null
        )
        val contactJson = objectMapper.writeValueAsString(contactDTO)

        // Act and Assert
        mockMvc.patch("/api/contacts/$id") {
            contentType = MediaType.APPLICATION_JSON
            content = contactJson
        }.andExpect {
            status { isOk() }
            jsonPath("$.contact", Matchers.notNullValue())
            jsonPath("$.contact.name", Matchers.`is`(contactDTO.name))
            jsonPath("$.contact.surname", Matchers.`is`(contactDTO.surname))
            jsonPath("$.contact.category", Matchers.`is`(contactDTO.category.name))
            jsonPath("$.contact.comment", Matchers.`is`(contactDTO.comment))
            jsonPath("$.contact.emails", Matchers.hasSize<Int>(Matchers.greaterThan(0)))
        }
    }

    @Test
    fun `update contact category should return status OK`() {
        // Arrange
        val id = 51L
        val contactDTO = mapOf(
            "category" to Category.CUSTOMER,
            "customer" to CCustomerDTO(),
        )
        val contactJson = objectMapper.writeValueAsString(contactDTO)

        // Act and Assert
        mockMvc.patch("/api/contacts/$id") {
            contentType = MediaType.APPLICATION_JSON
            content = contactJson
        }.andExpect {
            status { isOk() }
            jsonPath("$.contact", Matchers.notNullValue())
            jsonPath("$.contact.category", Matchers.`is`("CUSTOMER"))
        }
    }

    @Test
    fun `update contact category should return status BADREQUEST`() {
        // Arrange
        val id = 101L
        val contactDTO = mapOf(
            "category" to Category.UNKNOWN,
        )
        val contactJson = objectMapper.writeValueAsString(contactDTO)

        // Act and Assert
        mockMvc.patch("/api/contacts/$id") {
            contentType = MediaType.APPLICATION_JSON
            content = contactJson
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.description", Matchers.`is`("It is not possible to change the category of a contact"))
        }
    }

    @Test
    fun `update contact ssn code should return status BAD_REQUEST`() {
        // Arrange
        val id = 1L
        val contactDTO = mapOf(
            "ssnCode" to "123456789",
        )
        val contactJson = objectMapper.writeValueAsString(contactDTO)

        // Act and Assert
        mockMvc.patch("/api/contacts/$id") {
            contentType = MediaType.APPLICATION_JSON
            content = contactJson
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.description", Matchers.`is`("SSN code length must be 16 characters"))
        }
    }

    @Test
    fun `add email to contact should return status CREATED`() {
        // Arrange
        val id = 1L
        val emailDTO = CEmailDTO("mario.rossi@dd.cc")
        val emailJson = objectMapper.writeValueAsString(emailDTO)

        // Act and Assert
        mockMvc.post("/api/contacts/$id/email") {
            contentType = MediaType.APPLICATION_JSON
            content = emailJson
        }.andExpect {
            status { isCreated() }
            jsonPath("$.contact", Matchers.notNullValue())
        }
    }

    @Test
    fun `add email to contact should return status BAD_REQUEST`() {
        // Arrange
        val id = 1L
        val email = "mario.rossi"
        val emailDTO = mapOf("email" to email)
        val emailJson = objectMapper.writeValueAsString(emailDTO)

        // Act and Assert
        mockMvc.post("/api/contacts/$id/email") {
            contentType = MediaType.APPLICATION_JSON
            content = emailJson
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.message", Matchers.`is`("Validation error"))
            jsonPath("$.statusCode", Matchers.`is`(400))
            jsonPath("$.description", Matchers.`is`("Invalid email"))
        }
    }

    @Test
    fun `delete email from contact should return status NO_CONTENT`() {
        // Arrange
        val id = 1L
        val emailId = 1L

        // Act and Assert
        mockMvc.request(HttpMethod.DELETE, "/api/contacts/$id/email/$emailId") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNoContent() }
        }
    }

    @Test
    fun `add telephone to contact should return status CREATED`() {
        // Arrange
        val id = 1L
        val telephone = "3234567890"
        val telephoneDTO = mapOf("telephone" to telephone)
        val telephoneJson = objectMapper.writeValueAsString(telephoneDTO)

        // Act and Assert
        mockMvc.post("/api/contacts/$id/telephone") {
            contentType = MediaType.APPLICATION_JSON
            content = telephoneJson
        }.andExpect {
            status { isCreated() }
            jsonPath("$.contact", Matchers.notNullValue())
        }
    }

    @Test
    fun `add telephone to contact should return status BAD_REQUEST`() {
        // Arrange
        val id = 1L
        val telephone = "a23456789"
        val telephoneDTO = mapOf("telephone" to telephone)
        val telephoneJson = objectMapper.writeValueAsString(telephoneDTO)

        // Act and Assert
        mockMvc.post("/api/contacts/$id/telephone") {
            contentType = MediaType.APPLICATION_JSON
            content = telephoneJson
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.message", Matchers.`is`("Validation error"))
            jsonPath("$.statusCode", Matchers.`is`(400))
            jsonPath("$.description", Matchers.`is`("Invalid telephone"))
        }
    }

    @Test
    fun `delete telephone from contact should return status NO_CONTENT`() {
        // Arrange
        val id = 1L
        val telephoneId = 1L

        // Act and Assert
        mockMvc.request(HttpMethod.DELETE, "/api/contacts/$id/telephone/$telephoneId") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNoContent() }
        }
    }

    @Test
    fun `add address to contact should return status CREATED`() {
        // Arrange
        val id = 1L
        val address = "Via Roma 1"
        val addressDTO = mapOf("address" to address)
        val addressJson = objectMapper.writeValueAsString(addressDTO)

        // Act and Assert
        mockMvc.post("/api/contacts/$id/address") {
            contentType = MediaType.APPLICATION_JSON
            content = addressJson
        }.andExpect {
            status { isCreated() }
            jsonPath("$.contact", Matchers.notNullValue())
        }
    }

    @Test
    fun `delete address from contact should return status NO_CONTENT`() {
        // Arrange
        val id = 1L
        val addressId = 1L

        // Act and Assert
        mockMvc.request(HttpMethod.DELETE, "/api/contacts/$id/address/$addressId") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNoContent() }
        }
    }

    @Test
    fun `delete address from contact should return status NOT_FOUND`() {
        // Arrange
        val id = 1L
        val addressId = 2L

        // Act and Assert
        mockMvc.request(HttpMethod.DELETE, "/api/contacts/$id/address/$addressId") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.message", Matchers.`is`("Entity not found"))
            jsonPath("$.statusCode", Matchers.`is`(404))
            jsonPath("$.description", Matchers.`is`("Address not found"))
        }
    }

    @Test
    fun `delete telephone from contact should return status NOT_FOUND`() {
        // Arrange
        val id = 1L
        val telephoneId = 2L

        // Act and Assert
        mockMvc.request(HttpMethod.DELETE, "/api/contacts/$id/telephone/$telephoneId") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.message", Matchers.`is`("Entity not found"))
            jsonPath("$.statusCode", Matchers.`is`(404))
            jsonPath("$.description", Matchers.`is`("Telephone not found"))
        }
    }
}