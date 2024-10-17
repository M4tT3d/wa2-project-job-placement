package org.example.analytics.dtos.request.create

data class CJobOfferDTO(
    val jobOfferId: Long,
    val value: Float,
    val duration: Float
)
