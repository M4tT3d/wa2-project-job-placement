package org.example.analytics.services

import org.example.analytics.dtos.SkillJobOfferDTO
import org.example.analytics.dtos.request.create.CSkillJobOfferDTO
import org.example.analytics.dtos.request.delete.DSkillJobOfferDTO
import org.example.analytics.dtos.request.update.USkillJobOfferDTO

interface SkillJobOfferService {

    fun listAll(): List<SkillJobOfferDTO>

    fun mostAndLeastRequest(): Map<String, String>

    fun addSkillJobOffer(newSkillJobOffer: CSkillJobOfferDTO): List<SkillJobOfferDTO>

    fun updateNameSkillJobOffer(skillUpdate: USkillJobOfferDTO): SkillJobOfferDTO

    fun deleteSkillJobOffer(skillD: DSkillJobOfferDTO)

    fun decreaseRateSkillJobOffer(skillD: DSkillJobOfferDTO)
}