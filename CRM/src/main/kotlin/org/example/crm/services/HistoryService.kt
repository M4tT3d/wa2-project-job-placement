package org.example.crm.services

import org.example.crm.dtos.HistoryDTO
import org.example.crm.dtos.request.create.CHistoryDTO

interface HistoryService {
    fun create(createHistoryDTO: CHistoryDTO): HistoryDTO

    fun findHistoryByMessageId(pageNumber: Int, limit: Int, messageId: Long): List<HistoryDTO>
}