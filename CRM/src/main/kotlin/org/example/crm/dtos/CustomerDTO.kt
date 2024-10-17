package org.example.crm.dtos

import org.example.crm.entities.Customer

data class CustomerDTO(
    val id: Long,
    val comment: String? = null,
) {
    fun toResponseEntity() = mapOf(
        "id" to id,
        "comment" to comment
    )
}

fun Customer.toDTO() = CustomerDTO(
    id = id,
    comment = comment
)