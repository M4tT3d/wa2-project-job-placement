package org.example.analytics.dtos.request.update

data class UJobOfferDurationDTO(
    val jobOfferId: Long,
    val duration: Float
)

data class UJobOfferValueDTO(
    val jobOfferId: Long,
    val value: Float
)
