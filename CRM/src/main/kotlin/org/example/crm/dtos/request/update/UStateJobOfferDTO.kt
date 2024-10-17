package org.example.crm.dtos.request.update

import org.example.crm.utils.enums.StateJobOffer

data class UStateJobOfferDTO(
    val status: StateJobOffer,
    val professionalId: Long? = null
)