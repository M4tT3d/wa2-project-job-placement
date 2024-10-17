package org.example.analytics.services

import org.example.analytics.dtos.SkillProfessionalDTO
import org.example.analytics.dtos.request.create.CSkillProfessionalDTO
import org.example.analytics.dtos.request.delete.DSkillProfessionalDTO
import org.example.analytics.dtos.request.update.USkillProfessionalDTO

interface SkillProfessionalService {

    fun listAll(): List<SkillProfessionalDTO>

    fun mostAndLeastRequest(): Map<String, String>

    fun addSkillProfessional(newSkillProfessional: CSkillProfessionalDTO): List<SkillProfessionalDTO>

    fun updateNameSkillProfessional(skillUpdate: USkillProfessionalDTO): SkillProfessionalDTO

    fun deleteSkillProfessional(skillDelete: DSkillProfessionalDTO)

    fun decreaseRateSkillProfessional(skillDecrease: DSkillProfessionalDTO)
}