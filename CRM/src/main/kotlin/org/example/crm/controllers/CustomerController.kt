package org.example.crm.controllers

import org.example.crm.dtos.request.filters.CustomerParams
import org.example.crm.dtos.request.filters.PaginationParams
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
@RequestMapping("/api/customers")
class CustomerController(private val contactService: ContactService) {
    private val logger = LoggerFactory.getLogger(CustomerController::class.java)

    @GetMapping(
        "",
        "/",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getAllCustomers(
        @Validated paginationParams: PaginationParams,
        @Validated params: CustomerParams
    ): ResponseEntity<Map<String, Any?>> {
        logger.info("Getting all customers")
        val customers =
            contactService.getCustomers(paginationParams, params).map { it.toResponse() }
        logger.info("Customers retrieved")
        return ResponseEntity(mapOf("customers" to customers.ifEmpty { null }), HttpStatus.OK)
    }
}