package org.example.crm.dtos.request.create

import jakarta.validation.constraints.NotBlank

data class CSkillDTO(
    @field:NotBlank(message = "Skill cannot be empty")
    val skill: String
)
