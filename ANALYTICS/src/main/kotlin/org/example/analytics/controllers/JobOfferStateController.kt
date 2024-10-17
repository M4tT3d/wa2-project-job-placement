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
@RequestMapping("api/job-offer-state")
class JobOfferStateController(
    val customerService: CustomerService
) {

    private val logger = LoggerFactory.getLogger(JobOfferStateController::class.java)

    @GetMapping("/", "")
    fun getJobOffer(): ResponseEntity<List<Map<String, Any?>>> {
        val response = customerService.getJobOffer().toResponse()
        logger.info("Retrieving state of job offer")
        return ResponseEntity(response, HttpStatus.OK)
    }
}