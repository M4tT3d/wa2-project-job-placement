package org.example.crm.dtos.request.create

import org.example.crm.validators.NullAndNotEmpty

data class CCustomerDTO(
    @field:NullAndNotEmpty(message = "Name is required")
    val comment: String? = null,
)
