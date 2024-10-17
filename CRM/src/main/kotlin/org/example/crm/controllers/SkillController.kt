package org.example.crm.controllers

import org.example.crm.dtos.SkillDTO
import org.example.crm.dtos.request.create.CSkillDTO
import org.example.crm.services.SkillService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/skills")
class SkillController(private val skillService: SkillService) {
    private val logger = LoggerFactory.getLogger(SkillController::class.java)

    @PostMapping(
        "",
        "/",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun addSkill(
        @Validated @RequestBody skill: CSkillDTO
    ): Map<String, Any> {
        logger.info("Adding skill")
        val newSkill = skillService.addSkill(skill.skill)
        logger.info("Skill added")
        return mapOf("skill" to newSkill)
    }

    @GetMapping(
        "",
        "/",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getSkills(): ResponseEntity<Map<String, List<SkillDTO>?>> {
        logger.info("Getting skills")
        val skills = skillService.getSkills()
        logger.info("Skills retrieved")
        return ResponseEntity(mapOf("skills" to skills), HttpStatus.OK)
    }

    @GetMapping(
        "/{skillId}",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getSkillById(
        @PathVariable skillId: Long
    ): ResponseEntity<Map<String, SkillDTO>> {
        logger.info("Getting skill $skillId")
        val skill = skillService.getSkillById(skillId)
        logger.info("Skill retrieved")
        return ResponseEntity(mapOf("skill" to skill), HttpStatus.OK)
    }

    @PutMapping(
        "/{skillId}",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updateSkill(
        @PathVariable skillId: Long,
        @Validated @RequestBody skill: CSkillDTO
    ): ResponseEntity<Any> {
        logger.info("Updating skill $skillId")
        if (skill.skill.isBlank()) {
            logger.info("Skill cannot be empty")
            return ResponseEntity(mapOf("description" to "Skill name cannot be empty"), HttpStatus.BAD_REQUEST)
        }
        val updatedSkill = skillService.updateSkill(skillId, skill.skill)
        logger.info("Skill $skillId updated")
        return ResponseEntity(
            mapOf("skill" to updatedSkill),
            HttpStatus.OK
        )
    }

    @DeleteMapping(
        "/{skillId}",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun deleteSkill(
        @PathVariable skillId: Long
    ): ResponseEntity<Any> {
        logger.info("Deleting skill $skillId")
        skillService.deleteSkill(skillId)
        logger.info("Skill deleted")
        return ResponseEntity.noContent().build()
    }
}