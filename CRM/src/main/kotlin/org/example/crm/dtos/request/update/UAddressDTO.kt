package org.example.crm.dtos.request.update

import org.example.crm.validators.NullAndNotEmpty

data class UAddressDTO(
    @field:NullAndNotEmpty(message = "Address cannot be empty")
    val address: String? = null,
    @field:NullAndNotEmpty(message = "Comment cannot be empty")
    val comment: String? = null
)