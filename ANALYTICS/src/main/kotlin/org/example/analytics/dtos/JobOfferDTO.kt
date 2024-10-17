package org.example.analytics.dtos

import org.example.analytics.documents.JobOffer

data class JobOfferDTO(
    val id: String?,
    val jobOfferId: Long,
    val value: Float,
    val duration: Float
)

fun JobOffer.toDTO(): JobOfferDTO =
    JobOfferDTO(
        this.id,
        this.jobOfferId,
        this.value,
        this.duration
    )

fun JobOfferDTO.toResponse(): Map<String, Any?> =
    mapOf(
        "id" to this.id,
        "jobOfferId" to this.jobOfferId,
        "value" to this.value,
        "duration" to this.duration
    )

