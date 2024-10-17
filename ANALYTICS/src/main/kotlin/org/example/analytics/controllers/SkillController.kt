package org.example.analytics.controllers

import org.example.analytics.dtos.toCombine
import org.example.analytics.dtos.toResponse
import org.example.analytics.services.SkillJobOfferService
import org.example.analytics.services.SkillProfessionalService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/skill")
class SkillController(
    val skillJobOfferService: SkillJobOfferService,
    val skillProfessionalService: SkillProfessionalService
) {

    private val logger = LoggerFactory.getLogger(SkillController::class.java)

    // JOB OFFER
    @GetMapping("/jo", "/jo/")
    fun listAllJO(): ResponseEntity<List<Map<String, Any?>>> {
        val response = skillJobOfferService.listAll().map {
            it.toResponse()
        }
        logger.info("Retrieving skills of job offers")
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping("/jo/mostAndLeast", "jo/mostAndLeast/")
    fun mostAndLeastRequestJO(): ResponseEntity<Map<String, Any?>> {
        val response = skillJobOfferService.mostAndLeastRequest()
        logger.info("Retrieving most and least requested skills of job offers")
        return ResponseEntity(response, HttpStatus.OK)
    }

    // PROFESSIONAL
    @GetMapping("/p", "/p/")
    fun listAllP(): ResponseEntity<List<Map<String, Any?>>> {
        val response = skillProfessionalService.listAll().map {
            it.toResponse()
        }
        logger.info("Retrieving skills of professionals")
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping("/p/mostAndLeast", "/p/mostAndLeast/")
    fun mostAndLeastRequestP(): ResponseEntity<Map<String, Any?>> {
        val response = skillProfessionalService.mostAndLeastRequest()
        logger.info("Retrieving most and least requested skills of professionals")
        return ResponseEntity(response, HttpStatus.OK)
    }

    //COMBINED
    @GetMapping("/all", "/all/")
    fun listAll(): ResponseEntity<List<Map<String, Any?>>> {
        val skillJO = skillJobOfferService.listAll().map {
            it.toCombine()
        }
        val skillP = skillProfessionalService.listAll().map {
            it.toCombine()
        }
        val response = convertToGraph(skillJO, skillP)
        logger.info("Retrieving all skills")
        return ResponseEntity(response, HttpStatus.OK)
    }
}

fun convertToGraph(skillsJO: List<Map<String, Any?>>, skillsP: List<Map<String, Any?>>): List<Map<String, Any?>> {
    val response = skillsJO.map { it.toMutableMap() }.toMutableList()
    var added: Int
    for (skillP in skillsP) {
        added = 0
        for (skill in response) {
            if (skill["name"] == skillP["name"]) {
                skill["professional"] = skillP["professional"]
                added = 1
                break
            }
        }
        if (added == 0) {
            val new = mapOf(
                "name" to skillP["name"],
                "jobOffer" to 0,
                "professional" to skillP["professional"]
            )
            response.add(new.toMutableMap())

        }

    }
    return response
}