package org.example.analytics.services

import org.example.analytics.documents.SkillProfessional
import org.example.analytics.dtos.SkillProfessionalDTO
import org.example.analytics.dtos.request.create.CSkillProfessionalDTO
import org.example.analytics.dtos.request.delete.DSkillProfessionalDTO
import org.example.analytics.dtos.request.update.USkillProfessionalDTO
import org.example.analytics.dtos.toDTO
import org.example.analytics.repositories.SkillProfessionalRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SkillProfessionalServiceImpl(
    private val skillProfessionalRepository: SkillProfessionalRepository
) : SkillProfessionalService {

    override fun listAll(): List<SkillProfessionalDTO> {
        return skillProfessionalRepository.findAll().map {
            it.toDTO()
        }
    }

    override fun mostAndLeastRequest(): Map<String, String> {
        val skillJobObber = skillProfessionalRepository.findAll()
        if (skillJobObber.isEmpty()) {
            return mapOf(
                "mostRequest" to "none",
                "leastRequest" to "none"
            )
        } else {
            val mostRequest = skillJobObber.maxBy { it.rate }
            val leastRequest = skillJobObber.minBy { it.rate }
            return mapOf(
                "mostRequest" to mostRequest.name,
                "leastRequest" to leastRequest.name
            )
        }
    }


    override fun addSkillProfessional(newSkillProfessional: CSkillProfessionalDTO): List<SkillProfessionalDTO> {
        var skillProfessional = skillProfessionalRepository.findBySkillId(newSkillProfessional.skillId)
        if (skillProfessional == null) {
            skillProfessional = SkillProfessional(
                skillId = newSkillProfessional.skillId,
                name = newSkillProfessional.name,
                rate = 1
            )
        } else {
            skillProfessional.rate += 1
        }
        skillProfessionalRepository.save(skillProfessional)
        return skillProfessionalRepository.findAll().map { it.toDTO() }
    }

    override fun updateNameSkillProfessional(skillUpdate: USkillProfessionalDTO): SkillProfessionalDTO {
        val skillP = skillProfessionalRepository.findBySkillId(skillUpdate.skillId)
        if (skillP != null) {
            skillP.name = skillUpdate.name
            skillProfessionalRepository.save(skillP)
        }
        return SkillProfessionalDTO(
            id = null,
            skillId = -1,
            name = "",
            rate = -1
        )
    }

    override fun deleteSkillProfessional(skillDelete: DSkillProfessionalDTO) {
        val skillP = skillProfessionalRepository.findBySkillId(skillDelete.skillId)
        if (skillP != null) {
            skillProfessionalRepository.delete(skillP)
        }
    }

    override fun decreaseRateSkillProfessional(skillDecrease: DSkillProfessionalDTO) {
        val skillP = skillProfessionalRepository.findBySkillId(skillDecrease.skillId)
        if (skillP != null) {
            skillP.rate -= 1
            if (skillP.rate == 0L) {
                skillProfessionalRepository.delete(skillP)
            } else {
                skillProfessionalRepository.save(skillP)
            }
        }
    }
}