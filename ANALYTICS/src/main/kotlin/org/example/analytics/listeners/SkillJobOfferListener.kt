package org.example.analytics.listeners

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.example.analytics.dtos.request.create.CSkillJobOfferDTO
import org.example.analytics.dtos.request.delete.DSkillJobOfferDTO
import org.example.analytics.dtos.request.update.USkillJobOfferDTO
import org.example.analytics.services.SkillJobOfferService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class SkillJobOfferListener(
    val skillJobOfferService: SkillJobOfferService
) {
    private val logger = LoggerFactory.getLogger(SkillJobOfferListener::class.java)
    private val mapper = jacksonObjectMapper()

    @KafkaListener(
        id = "jo_skills",
        topics = ["jo_skills"],
        groupId = "analytics"
    )
    fun skillJobOffer(skillJobOfferString: String) {
        val skillJOMap: Map<String, Any> = mapper.readValue(skillJobOfferString)
        logger.info("Received message on jo_skills topic")
        if (skillJOMap.size == 1) {
            val skillJOValueString =
                skillJOMap.values.toString().removeSurrounding("[", "]").split(";").map { it.trim() }
            for (skill in skillJOValueString) {
                val objectSkill =
                    skill.split(",").map { it.trim().split(":").map { it.trim() } }.associate { it[0] to it[1] }
                val skillJODTO = CSkillJobOfferDTO(
                    skillId = objectSkill["id"]!!.toLong(),
                    name = objectSkill["name"].toString()
                )
                skillJobOfferService.addSkillJobOffer(skillJODTO)
            }
        } else if (skillJOMap.size == 2) {
            val skillOlds =
                skillJOMap["oldSkills"].toString().removeSurrounding("[", "]").split("-").map { it.trim() }
            for (skillOld in skillOlds) {
                val skillD = DSkillJobOfferDTO(
                    skillId = skillOld.toLong()
                )
                skillJobOfferService.decreaseRateSkillJobOffer(skillD)
            }
            val skillNews =
                skillJOMap["newSkills"].toString().removeSurrounding("[", "]").split(";").map { it.trim() }
            for (skillNew in skillNews) {
                val objectSkill =
                    skillNew.split(",").map { it.trim().split(":").map { it.trim() } }.associate { it[0] to it[1] }
                val skillJODTO = CSkillJobOfferDTO(
                    skillId = objectSkill["id"]!!.toLong(),
                    name = objectSkill["name"].toString()
                )
                skillJobOfferService.addSkillJobOffer(skillJODTO)
                logger.info("Job offer updated")
            }
        }
    }

    @KafkaListener(
        id = "u_skillsJO",
        topics = ["u_skills"],
        groupId = "analytics_updateSkillJO"
    )
    fun updateSkillJobOffer(skillJobOfferString: String) {
        val skillJOMap: Map<String, Any?> = mapper.readValue(skillJobOfferString)
        logger.info("Received message on u_skills topic")
        if (skillJOMap.size == 1) {
            val skillD = mapper.readValue(skillJobOfferString, DSkillJobOfferDTO::class.java)
            skillJobOfferService.deleteSkillJobOffer(skillD)
            logger.info("Skill job offer deleted")
        } else if (skillJOMap.size == 2) {
            val skillUpdate = mapper.readValue(skillJobOfferString, USkillJobOfferDTO::class.java)
            skillJobOfferService.updateNameSkillJobOffer(skillUpdate)
            logger.info("Skill job offer updated")
        }
    }
}