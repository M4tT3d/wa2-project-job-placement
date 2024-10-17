package org.example.crm.services

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.example.crm.dtos.HistoryDTO
import org.example.crm.dtos.request.create.CHistoryDTO
import org.example.crm.dtos.toDto
import org.example.crm.entities.History
import org.example.crm.repositories.HistoryRepository
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.stereotype.Service

@Service
@Transactional
@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
class HistoryServiceImpl(
    private val historyRepository: HistoryRepository
) : HistoryService {

    override fun create(createHistoryDTO: CHistoryDTO): HistoryDTO {
        val newHistoryEntity = History(createHistoryDTO)
        return historyRepository.save(newHistoryEntity).toDto()
    }

    override fun findHistoryByMessageId(pageNumber: Int, limit: Int, messageId: Long): List<HistoryDTO> {
        val sorting = Sort.by(Sort.Order.desc("date"))
        val page = PageRequest.of(pageNumber, limit, sorting)
        val result = historyRepository.findByMessages_Id(page, messageId)
        if (result.isEmpty()) {
            throw EntityNotFoundException("This id (${messageId}) does not exist")
        }
        return result.map { it.toDto() }
    }
}