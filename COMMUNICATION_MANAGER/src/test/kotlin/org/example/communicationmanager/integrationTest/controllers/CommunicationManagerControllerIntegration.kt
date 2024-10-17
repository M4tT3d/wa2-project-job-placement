package org.example.communicationmanager.integrationTest.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.communicationmanager.dtos.CMailDTO
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@AutoConfigureMockMvc
@SpringBootTest
class CommunicationManagerControllerIntegration {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper


    @Test
    fun sendEmail() {
        val email = CMailDTO(
            receiver = "s317748@studenti.polito.it",
            subject = "Test",
            body = "Test"
        )
        val emailJson = objectMapper.writeValueAsString(email)

        mockMvc.post("/api/emails") {
            contentType = MediaType.APPLICATION_JSON
            content = emailJson
        }.andExpect {
            status { isNoContent() }
            content { string("") }
        }
    }

    @Test
    fun sendEmailWithEmailNotValid() {
        val email = mapOf(
            "receiver" to "s317748",
            "subject" to "Test",
            "body" to "Test"
        )
        val emailJson = objectMapper.writeValueAsString(email)
        mockMvc.post("/api/emails") {
            contentType = MediaType.APPLICATION_JSON
            content = emailJson
        }.andExpect {
            status { isBadRequest() }
        }
    }
}