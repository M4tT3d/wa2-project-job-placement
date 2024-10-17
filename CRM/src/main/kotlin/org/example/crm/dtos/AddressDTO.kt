package org.example.crm.dtos

import org.example.crm.entities.Address

data class AddressDTO(
    val id:Long,
    val address:String,
    val comment:String?
)
fun Address.toDTO() = AddressDTO(
    id = id,
    address = address,
    comment = comment
)