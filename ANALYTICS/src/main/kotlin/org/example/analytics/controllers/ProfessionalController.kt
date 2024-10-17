package org.example.analytics.controllers

import org.example.analytics.dtos.toResponse
import org.example.analytics.services.ProfessionalService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/professional")
class ProfessionalController(
    val professionalService: ProfessionalService
) {

    private val logger = LoggerFactory.getLogger(ProfessionalController::class.java)

    @GetMapping("/", "")
    fun listAll(): ResponseEntity<List<Map<String, Any?>>> {
        val response = professionalService.getProfessional().toResponse()
        logger.info("Retrieving all professionals")
        return ResponseEntity(response, HttpStatus.OK)
    }
}