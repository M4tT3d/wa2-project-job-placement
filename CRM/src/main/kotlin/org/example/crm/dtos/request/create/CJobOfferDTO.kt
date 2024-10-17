package org.example.crm.dtos.request.create

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class CJobOfferDTO(
    @field:NotBlank(message = "description is required")
    val description: String,
    @field:NotEmpty(message = "Skills cannot be empty")
    val skills: Set<Long>,
    val comment: String? = null,
    @field:Min(1, message = "Duration must be greater than 1.0")
    val duration: Float,
    @field:Min(0, message = "Customer id must be greater than 0")
    val customerId: Long,
)