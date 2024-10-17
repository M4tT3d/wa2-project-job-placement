package org.example.crm.dtos

import org.example.crm.entities.Telephone

data class TelephoneDTO(
    val id:Long,
    val telephone:String,
    val comment:String?
)
fun Telephone.toDTO() = TelephoneDTO(
    id = id,
    telephone = telephone,
    comment = comment
)