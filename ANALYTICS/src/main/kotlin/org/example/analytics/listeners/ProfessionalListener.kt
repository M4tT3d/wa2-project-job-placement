package org.example.analytics.listeners

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.example.analytics.dtos.request.create.CProfessionalDTO
import org.example.analytics.dtos.request.delete.DProfessionalDTO
import org.example.analytics.dtos.request.delete.DSkillProfessionalDTO
import org.example.analytics.dtos.request.update.UProfessionalDTO
import org.example.analytics.services.ProfessionalService
import org.example.analytics.services.SkillProfessionalService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class ProfessionalListener(
    val professionalService: ProfessionalService,
    val skillProfessionalService: SkillProfessionalService
) {
    private val logger = LoggerFactory.getLogger(ProfessionalListener::class.java)
    private val mapper = jacksonObjectMapper()

    @KafkaListener(
        id = "contact_professional",
        topics = ["contact_professional"],
        groupId = "analytics"
    )
    fun professional(professionalString: String) {
        val professionalMap: Map<String, Any> = mapper.readValue(professionalString)
        logger.info("Received message on contact_professional topic")
        if (professionalMap.size == 1) {
            val professional: CProfessionalDTO = mapper.readValue(professionalString, CProfessionalDTO::class.java)
            professionalService.create(professional)
            logger.info("Professional created")

        } else if (professionalMap.size == 2) {
            val professional: UProfessionalDTO = mapper.readValue(professionalString, UProfessionalDTO::class.java)
            professionalService.update(professional)
            logger.info("Professional updated")
        }
    }

    @KafkaListener(
        id = "delete_professional",
        topics = ["delete_professional"],
        groupId = "analytics"
    )
    fun deleteProfessional(professionalString: String) {
        val professional: DProfessionalDTO = mapper.readValue(professionalString, DProfessionalDTO::class.java)
        logger.info("Received message on delete_professional topic")
        professionalService.delete(professional)
        val skillValue = professional.deleteSkills.split(";").map { it.trim() }
        for (skillString in skillValue) {
            logger.info("Decreasing rate of skill: $skillString")
            val skillDTO = DSkillProfessionalDTO(
                skillId = skillString.trim().toLong(),
            )
            skillProfessionalService.decreaseRateSkillProfessional(skillDTO)
        }
    }
}