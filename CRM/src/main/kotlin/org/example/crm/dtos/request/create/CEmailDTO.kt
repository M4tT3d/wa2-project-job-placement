package org.example.crm.dtos.request.create

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.example.crm.validators.NullAndNotEmpty

data class CEmailDTO(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email")
    val email: String,
    @field:NullAndNotEmpty(message = "Comment cannot be empty")
    val comment: String? = null
)
