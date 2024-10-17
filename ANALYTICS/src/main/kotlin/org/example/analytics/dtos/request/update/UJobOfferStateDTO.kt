package org.example.analytics.dtos.request.update

import org.example.analytics.utils.StateJobOffer

data class UJobOfferStateDTO(
    val customerId: Long,
    val jobOfferState_old: StateJobOffer,
    val jobOfferState_new: StateJobOffer
)
