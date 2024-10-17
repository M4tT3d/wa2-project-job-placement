package org.example.analytics.dtos

import org.example.analytics.documents.SkillProfessional

data class SkillProfessionalDTO(
    val id: String?,
    val skillId: Long,
    val name: String,
    val rate: Long,
)

fun SkillProfessional.toDTO(): SkillProfessionalDTO =
    SkillProfessionalDTO(
        this.id,
        this.skillId,
        this.name,
        this.rate,
    )

fun SkillProfessionalDTO.toResponse(): Map<String, Any?> =
    mapOf(
        "id" to this.id,
        "skillId" to this.skillId,
        "name" to this.name,
        "rate" to this.rate,
    )

fun SkillProfessionalDTO.toCombine(): Map<String, Any?> =
    mapOf(
        "name" to this.name,
        "professional" to this.rate,
        "jobOffer" to 0,
    )