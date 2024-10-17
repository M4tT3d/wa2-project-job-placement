package org.example.crm.dtos

import org.example.crm.entities.History
import org.example.crm.entities.Message
import org.example.crm.utils.enums.Channel
import org.example.crm.utils.enums.Priority
import org.example.crm.utils.enums.State
import java.util.*

data class MessageDTO(
    val id: Long,
    val sender: String,
    val date: Date,
    val subject: String?,
    val body: String?,
    val channel: Channel,
    val priority: Priority?,
    var state: State,
    val comment: String?,
    val histories: MutableList<History>

)

fun Message.toDto(): MessageDTO =
    MessageDTO(
        this.id,
        this.sender,
        this.date,
        this.subject,
        this.body,
        this.channel,
        this.priority,
        this.state,
        this.comment,
        this.histories
    )

fun MessageDTO.toResponse(): Map<String, Any?> =
    mapOf(
        "id" to this.id,
        "sender" to this.sender,
        "date" to this.date,
        "subject" to this.subject,
        "body" to this.body,
        "channel" to this.channel,
        "priority" to this.priority,
        "state" to this.state,
        "comment" to this.comment
    )