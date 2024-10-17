package org.example.crm.dtos.request.update

import org.example.crm.utils.enums.Transition


data class UStateMessageDTO(
    val transition: Transition,
    val comment: String? = null,
)