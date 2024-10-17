package org.example.analytics.listeners

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.example.analytics.dtos.request.create.CSkillProfessionalDTO
import org.example.analytics.dtos.request.delete.DSkillProfessionalDTO
import org.example.analytics.dtos.request.update.USkillProfessionalDTO
import org.example.analytics.services.SkillProfessionalService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class SkillProfessionalListener(
    val skillProfessionalService: SkillProfessionalService
) {
    private val logger = LoggerFactory.getLogger(SkillProfessionalListener::class.java)
    private val mapper = jacksonObjectMapper()

    @KafkaListener(
        id = "p_skills",
        topics = ["p_skills"],
        groupId = "analytics"
    )
    fun skillProfessional(skillProfessionalString: String) {
        val skillPMap: Map<String, Any?> = mapper.readValue(skillProfessionalString)
        logger.info("Received message on p_skills topic")
        if (skillPMap.size == 1) {
            val skillPValueString = skillPMap.values.toString().removeSurrounding("[", "]").split(";").map { it.trim() }
            for (skill in skillPValueString) {
                val objectSkill =
                    skill.split(",").map { it.trim().split(":").map { it.trim() } }.associate { it[0] to it[1] }
                val skillPDTO = CSkillProfessionalDTO(
                    skillId = objectSkill["id"]!!.toLong(),
                    name = objectSkill["name"].toString()
                )
                skillProfessionalService.addSkillProfessional(skillPDTO)
            }
        } else if (skillPMap.size == 2) {
            val skillOlds = skillPMap["oldSkills"].toString().removeSurrounding("[", "]").split("-").map { it.trim() }
            for (skillOld in skillOlds) {
                val skillD = DSkillProfessionalDTO(
                    skillId = skillOld.toLong()
                )
                skillProfessionalService.decreaseRateSkillProfessional(skillD)
            }
            val skillNews = skillPMap["newSkills"].toString().removeSurrounding("[", "]").split(";").map { it.trim() }
            for (skillNew in skillNews) {
                val objectSkill =
                    skillNew.split(",").map { it.trim().split(":").map { it.trim() } }.associate { it[0] to it[1] }
                val skillPDTO = CSkillProfessionalDTO(
                    skillId = objectSkill["id"]!!.toLong(),
                    name = objectSkill["name"].toString()
                )
                skillProfessionalService.addSkillProfessional(skillPDTO)
            }
            logger.info("Professional skill updated")
        }
    }

    @KafkaListener(
        id = "u_skillsP",
        topics = ["u_skills"],
        groupId = "analytics_updateSkillP"
    )
    fun updateSkillProfessional(skillProfessionalString: String) {
        val skillPMap: Map<String, Any?> = mapper.readValue(skillProfessionalString)
        logger.info("Received message on u_skills topic")
        if (skillPMap.size == 1) {
            val skillD = mapper.readValue(skillProfessionalString, DSkillProfessionalDTO::class.java)
            skillProfessionalService.deleteSkillProfessional(skillD)
            logger.info("Skill professional Deleted")
        } else {
            val skillUpdate = mapper.readValue(skillProfessionalString, USkillProfessionalDTO::class.java)
            skillProfessionalService.updateNameSkillProfessional(skillUpdate)
            logger.info("Skill professional updated")
        }
    }
}