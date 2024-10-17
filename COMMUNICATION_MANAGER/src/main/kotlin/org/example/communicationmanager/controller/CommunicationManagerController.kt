package org.example.communicationmanager.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.camel.ProducerTemplate
import org.example.communicationmanager.dtos.CMailDTO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class CommunicationManagerController {

    @Autowired
    lateinit var producerTemplate: ProducerTemplate

    private val logger = LoggerFactory.getLogger(CommunicationManagerController::class.java)

    private val mapper = jacksonObjectMapper()

    @PostMapping(
        "/emails",
        "/emails/",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun sendEmail(
        @RequestBody email: CMailDTO,
    ) {
        producerTemplate.sendBody("direct:/api/emails", email)
        logger.info("Email sended")
    }
}