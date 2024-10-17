package org.example.crm.controllers

import org.example.crm.dtos.request.filters.PaginationParams
import org.example.crm.dtos.request.filters.ProfessionalParams
import org.example.crm.services.ContactService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/professionals")
class ProfessionalController(private val contactService: ContactService) {
    private val logger = LoggerFactory.getLogger(ProfessionalController::class.java)

    @GetMapping(
        "/",
        "",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getAllProfessionals(
        @Validated paginationParams: PaginationParams,
        @Validated params: ProfessionalParams
    ): ResponseEntity<Any?> {
        logger.info("Getting all professionals")
        val professionals =
            contactService.getProfessionals(paginationParams, params).map { it.toResponse() }
        logger.info("Professionals retrieved")
        return ResponseEntity(mapOf("professionals" to professionals.ifEmpty { null }), HttpStatus.OK)
    }
}