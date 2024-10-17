package org.example.crm.dtos

import org.example.crm.entities.Email

data class EmailDTO(
    val id:Long,
    val email:String,
    val comment:String?
)
fun Email.toDTO() = EmailDTO(
    id = id,
    email = email,
    comment = comment
)