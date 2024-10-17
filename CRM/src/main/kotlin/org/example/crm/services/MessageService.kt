package org.example.crm.services


import org.example.crm.dtos.MessageDTO
import org.example.crm.dtos.request.create.CMessageDTO
import org.example.crm.dtos.request.filters.MessageParams
import org.example.crm.dtos.request.filters.PaginationParams
import org.example.crm.entities.Message
import org.example.crm.utils.enums.Priority


interface MessageService {
    fun create(createMessageDTO: CMessageDTO): MessageDTO
    fun listAll(
        paginationParams: PaginationParams,
        params: MessageParams,
    ): List<MessageDTO>

    fun findById(id: Long): Message
    fun updateState(id: Long, createMessageDTO: CMessageDTO, comment: String?): MessageDTO
    fun updatePriority(id: Long, priority: Priority): MessageDTO
}