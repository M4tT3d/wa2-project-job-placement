package org.example.crm.dtos

import org.example.crm.entities.Skill

data class SkillDTO(
    val id: Long,
    val skill: String,
)

fun Skill.toDTO() = SkillDTO(
    id = id,
    skill = skill,
)