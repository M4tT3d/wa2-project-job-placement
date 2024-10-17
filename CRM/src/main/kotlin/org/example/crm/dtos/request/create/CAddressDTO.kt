package org.example.crm.dtos.request.create

import jakarta.validation.constraints.NotBlank
import org.example.crm.validators.NullAndNotEmpty

data class CAddressDTO(
    @field:NotBlank(message = "Address is required")
    val address: String,
    @field:NullAndNotEmpty(message = "Comment cannot be empty")
    val comment: String? = null
)
