package org.example.crm.dtos.request.create

import org.example.crm.utils.enums.State
import java.util.*

data class CHistoryDTO(
    val comment: String? = null,
    val state: State,
    val date: Date,
)
