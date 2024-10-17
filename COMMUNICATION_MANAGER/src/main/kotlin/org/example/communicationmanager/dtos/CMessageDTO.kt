package org.example.communicationmanager.dtos

import java.util.*

data class CMessageDTO(
    val sender: String,
    val subject: String,
    val date: Date,
    val channel: String,
    val body: String,
)