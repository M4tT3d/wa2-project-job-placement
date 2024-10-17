package org.example.crm.dtos.request.update

import jakarta.validation.constraints.Email
import org.example.crm.validators.NullAndNotEmpty

data class UEmailDTO(
    @field:NullAndNotEmpty(message = "Email is required")
    @field:Email(message = "Invalid email")
    val email: String? = null,
    @field:NullAndNotEmpty(message = "Comment cannot be empty")
    val comment: String? = null
)
