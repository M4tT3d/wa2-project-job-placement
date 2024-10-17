package org.example.crm.dtos

import org.example.crm.entities.History
import org.example.crm.entities.Message
import org.example.crm.utils.enums.State
import java.util.*

data class HistoryDTO(
    val id: Long,
    val comment: String?,
    val state: State,
    val date: Date,
    val message: Message?

)

fun History.toDto(): HistoryDTO =
    HistoryDTO(this.id, this.comment, this.state, this.date, this.messages)

fun HistoryDTO.toResponse(): Map<String, Any?> =
    mapOf(
        "state" to this.state,
        "comment" to this.comment,
    )