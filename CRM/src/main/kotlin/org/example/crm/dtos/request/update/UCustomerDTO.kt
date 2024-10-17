package org.example.crm.dtos.request.update

import org.example.crm.validators.NullAndNotEmpty

data class UCustomerDTO(
    @field:NullAndNotEmpty(message = "Comment cannot be empty")
    val comment: String? = null
)
