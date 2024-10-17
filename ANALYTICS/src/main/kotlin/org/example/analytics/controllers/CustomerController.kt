package org.example.analytics.controllers

import org.example.analytics.dtos.toResponse
import org.example.analytics.services.CustomerService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/customer")
class CustomerController(
    val customerService: CustomerService
) {

    private val logger = LoggerFactory.getLogger(CustomerController::class.java)

    @GetMapping("/", "")
    fun listAll(): ResponseEntity<List<Map<String, Any?>>> {
        val response = customerService.listAll().map {
            it.toResponse()
        }
        logger.info("Retrieving all customers")
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping("/number")
    fun number(): ResponseEntity<Long> {
        val response = customerService.count()
        logger.info("Number of customers retrieved")
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping("/max")
    fun max(): ResponseEntity<Long> {
        val response = customerService.max()
        logger.info("Customer with most job offer retrieved")
        return ResponseEntity(response, HttpStatus.OK)
    }
}