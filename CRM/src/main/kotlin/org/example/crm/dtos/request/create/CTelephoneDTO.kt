package org.example.crm.dtos.request.create

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.example.crm.utils.MOBILEPHONE_REGEX
import org.example.crm.validators.NullAndNotEmpty

data class CTelephoneDTO(
    @field:NotBlank(message = "Telephone is required")
    @field:Pattern(regexp = MOBILEPHONE_REGEX, message = "Invalid telephone")
    val telephone: String,
    @field:NullAndNotEmpty(message = "Comment cannot be empty")
    val comment: String? = null
)
