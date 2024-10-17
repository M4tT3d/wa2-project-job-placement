package org.example.analytics.controllers

import org.example.analytics.dtos.toResponse
import org.example.analytics.services.JobOfferService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/job-offer")
class JobOfferController(
    val jobOfferService: JobOfferService
) {

    private val logger = LoggerFactory.getLogger(JobOfferController::class.java)

    @GetMapping("/", "")
    fun listAll(): ResponseEntity<List<Map<String, Any?>>> {
        val response = jobOfferService.listAll().map {
            it.toResponse()
        }
        logger.info("Retrieving all job offers")
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping("/max-min-average-value", "/max-min-average-value/")
    fun maxMinAverageValue(): ResponseEntity<Map<String, Any>> {
        val response = jobOfferService.maxMinAverageValue()
        logger.info("Retrieving max, min and average value of job offers")
        return ResponseEntity(response, HttpStatus.OK)
    }
}