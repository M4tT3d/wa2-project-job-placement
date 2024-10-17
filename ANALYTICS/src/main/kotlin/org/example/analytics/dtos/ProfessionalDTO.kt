package org.example.analytics.dtos

import org.example.analytics.documents.Professional

data class ProfessionalDTO(
    val id: String?,
    val employed: Long,
    val unemployed_available: Long,
    val not_available: Long,
    val rateAverage: Long,
)

fun Professional.toDTO(): ProfessionalDTO =
    ProfessionalDTO(
        this.id,
        this.employed,
        this.unemployed_available,
        this.not_available,
        this.rateAverage,
    )

fun ProfessionalDTO.toResponse(): List<Map<String, Any?>> =
    listOf(
        mapOf("state" to "employed", "value" to this.employed),
        mapOf("state" to "unemployed_available", "value" to this.unemployed_available),
        mapOf("state" to "not_available", "value" to this.not_available),
    )
