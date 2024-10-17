package org.example.communicationmanager.integrationTest.testRestTemplate

import org.example.communicationmanager.dtos.CMailDTO
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit4.SpringRunner


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommunicationManagerControllerTestRestTemplate {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun sendEmail() {
        val email = CMailDTO(
            receiver = "s317748@studenti.polito.it",
            subject = "Test",
            body = "Test"
        )
        val res: ResponseEntity<Any> = restTemplate.postForEntity("/api/emails", email)
        assert(res.statusCode == HttpStatus.NO_CONTENT)
        assert(res.body == null)
    }

    @Test
    fun sendEmailWithEmailNotValid() {
        val email = mapOf(
            "receiver" to "s317748",
            "subject" to "Test",
            "body" to "Test"
        )
        var res: ResponseEntity<Any> = restTemplate.postForEntity("/api/emails", email)
        assert(res.statusCode == HttpStatus.BAD_REQUEST)
    }
}