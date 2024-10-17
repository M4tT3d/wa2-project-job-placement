package org.example.analytics.services

import org.example.analytics.documents.SkillJobOffer
import org.example.analytics.dtos.SkillJobOfferDTO
import org.example.analytics.dtos.request.create.CSkillJobOfferDTO
import org.example.analytics.dtos.request.delete.DSkillJobOfferDTO
import org.example.analytics.dtos.request.update.USkillJobOfferDTO
import org.example.analytics.dtos.toDTO
import org.example.analytics.repositories.SkillJobOfferRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SkillJobOfferServiceImpl(
    private val skillJobOfferRepository: SkillJobOfferRepository
) : SkillJobOfferService {

    private val logger = LoggerFactory.getLogger(SkillJobOfferServiceImpl::class.java)
    override fun listAll(): List<SkillJobOfferDTO> {
        return skillJobOfferRepository.findAll().map { it.toDTO() }
    }

    override fun mostAndLeastRequest(): Map<String, String> {
        val skillJobObber = skillJobOfferRepository.findAll()
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

    override fun addSkillJobOffer(newSkillJobOffer: CSkillJobOfferDTO): List<SkillJobOfferDTO> {
        var skillJobOffer = skillJobOfferRepository.findBySkillId(newSkillJobOffer.skillId)
        if (skillJobOffer == null) {
            skillJobOffer = SkillJobOffer(
                skillId = newSkillJobOffer.skillId,
                name = newSkillJobOffer.name,
                rate = 1,
            )
        } else {
            skillJobOffer.rate += 1
        }
        skillJobOfferRepository.save(skillJobOffer)
        return skillJobOfferRepository.findAll().map { it.toDTO() }
    }

    override fun updateNameSkillJobOffer(skillUpdate: USkillJobOfferDTO): SkillJobOfferDTO {
        val skillJO = skillJobOfferRepository.findBySkillId(skillUpdate.skillId)
        if (skillJO != null) {
            skillJO.name = skillUpdate.name
            skillJobOfferRepository.save(skillJO)
        }
        return SkillJobOfferDTO(
            id = null,
            skillId = -1,
            name = "",
            rate = -1
        )
    }

    override fun deleteSkillJobOffer(skillD: DSkillJobOfferDTO) {
        val skillJO = skillJobOfferRepository.findBySkillId(skillD.skillId)
        if (skillJO != null) {
            skillJobOfferRepository.delete(skillJO)
        }
    }

    override fun decreaseRateSkillJobOffer(skillD: DSkillJobOfferDTO) {
        val skillJO = skillJobOfferRepository.findBySkillId(skillD.skillId)
        if (skillJO != null) {
            skillJO.rate -= 1
            if (skillJO.rate == 0L) {
                skillJobOfferRepository.delete(skillJO)
            } else {
                skillJobOfferRepository.save(skillJO)
            }
        }
    }
}