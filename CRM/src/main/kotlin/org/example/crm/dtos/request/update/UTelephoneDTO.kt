package org.example.crm.dtos.request.update

import jakarta.validation.constraints.Pattern
import org.example.crm.utils.MOBILEPHONE_REGEX
import org.example.crm.validators.NullAndNotEmpty


data class UTelephoneDTO(
    @field:NullAndNotEmpty(message = "Telephone is required")
    @field:Pattern(regexp = MOBILEPHONE_REGEX, message = "Invalid telephone")
    val telephone: String? = null,
    val comment: String? = null
)