package org.example.crm.dtos.request.update

import jakarta.validation.constraints.Min
import org.example.crm.utils.enums.StateJobOffer
import org.example.crm.validators.NullAndNotEmpty

data class UJobOfferDTO(
    @field:NullAndNotEmpty(message = "description is required")
    val description: String? = null,
    val skills: Set<Long>? = setOf(),
    val comment: String? = null,
    @field:Min(1, message = "Duration must be greater than 1.0")
    val duration: Float? = null,
    @field:Min(0, message = "Customer id must be greater than 0")
    val customerId: Long? = null,
    var status: StateJobOffer? = StateJobOffer.CREATED,
)
