package org.example.crm.services

import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.example.crm.dtos.SkillDTO
import org.example.crm.dtos.toDTO
import org.example.crm.entities.Skill
import org.example.crm.repositories.JobOfferRepository
import org.example.crm.repositories.ProfessionalRepository
import org.example.crm.repositories.SkillRepository
import org.example.crm.utils.DEFAULT_SKILLS
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.stereotype.Service

@Service
@Transactional
@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
class SkillServiceImpl(
    private val skillRepo: SkillRepository,
    private val jobOfferRepository: JobOfferRepository,
    private val professionalRepository: ProfessionalRepository,

    ) : SkillService {
    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, String>
    private val logger = LoggerFactory.getLogger(SkillService::class.java)

    @PostConstruct
    fun init() {
        if (skillRepo.count() == 0L)
            DEFAULT_SKILLS.forEach { skillRepo.save(Skill(it)) }
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun addSkill(newSkill: String): SkillDTO {
        val skill = skillRepo.save(Skill(newSkill))
        return skill.toDTO()
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun getSkills(): List<SkillDTO> {
        return skillRepo.findAll().map { it.toDTO() }
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun getSkillById(id: Long): SkillDTO {
        val skill = skillRepo.findById(id).orElse(null) ?: throw EntityNotFoundException("Skill not found")
        return skill.toDTO()
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun updateSkill(id: Long, newSkill: String): SkillDTO {
        val skill = skillRepo.findById(id).orElse(null) ?: throw EntityNotFoundException("Skill not found")
        skill.skill = newSkill
        try {
            val message = "{ \"skillId\":\"${id}\",\"name\":\"${newSkill}\"}"
            kafkaTemplate.send("u_skills", message)
            logger.info("kafka send message for updated skill")
        } catch (e: Exception) {
            logger.error("Error while sending message to Kafka", e)
        }
        return skill.toDTO()
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun deleteSkill(id: Long) {
        val skill = skillRepo.findById(id).orElse(null) ?: throw EntityNotFoundException("Skill not found")
        try {
            val message = "{ \"skillId\":\"${id}\"}"
            kafkaTemplate.send("u_skills", message)
            logger.info("kafka send message for deleted skill")
        } catch (e: Exception) {
            logger.error("Error while sending message to Kafka", e)
        }

        val professionals = professionalRepository.findAll().filter { it.skills.contains(skill) }
        professionals.forEach { professional ->
            professional.skills.remove(skill)
            professionalRepository.save(professional)
        }


        val jobOffers = jobOfferRepository.findAll().filter { it.skills.contains(skill) }
        jobOffers.forEach { jobOffer ->
            jobOffer.skills.remove(skill)
            jobOfferRepository.save(jobOffer)
        }
        skillRepo.deleteById(skill.id)
    }
}