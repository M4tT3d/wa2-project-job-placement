package org.example.analytics.dtos

import org.example.analytics.documents.SkillJobOffer

data class SkillJobOfferDTO(
    val id: String?,
    val skillId: Long,
    val name: String,
    val rate: Long,
)

fun SkillJobOffer.toDTO(): SkillJobOfferDTO =
    SkillJobOfferDTO(
        this.id,
        this.skillId,
        this.name,
        this.rate,
    )

fun SkillJobOfferDTO.toResponse(): Map<String, Any?> =
    mapOf(
        "id" to this.id,
        "skillId" to this.skillId,
        "name" to this.name,
        "rate" to this.rate,
    )

fun SkillJobOfferDTO.toCombine(): Map<String, Any?> =
    mapOf(
        "name" to this.name,
        "jobOffer" to this.rate,
        "professional" to 0,
    )