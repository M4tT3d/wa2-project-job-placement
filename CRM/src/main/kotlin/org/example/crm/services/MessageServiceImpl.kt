package org.example.crm.services


import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.example.crm.dtos.MessageDTO
import org.example.crm.dtos.request.create.CHistoryDTO
import org.example.crm.dtos.request.create.CMessageDTO
import org.example.crm.dtos.request.filters.MessageParams
import org.example.crm.dtos.request.filters.PaginationParams
import org.example.crm.dtos.toDto
import org.example.crm.entities.History
import org.example.crm.entities.Message
import org.example.crm.repositories.HistoryRepository
import org.example.crm.repositories.MessageRepository
import org.example.crm.utils.enums.Priority
import org.example.crm.utils.enums.State
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.stereotype.Service
import java.util.*

@Service
@Transactional
@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
class MessageServiceImpl(
    private val messageRepository: MessageRepository,
    private val historyRepository: HistoryRepository
) : MessageService {
    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun create(createMessageDTO: CMessageDTO): MessageDTO {
        val message = Message(createMessageDTO)
        messageRepository.save(message)
        val history = History(CHistoryDTO(state = State.RECEIVED, date = Date()))
        message.addHistory(history)
        historyRepository.save(history)
        return messageRepository.save(message).toDto()
    }
    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun listAll(
        paginationParams: PaginationParams,
        params: MessageParams,
    ): List<MessageDTO> {
        return messageRepository.findAll(
            params.toSpecification(),
            paginationParams.toPageRequest(params.toSorting())
        ).content.map { it.toDto() }
    }
    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun findById(id: Long): Message {
        val result = messageRepository.findById(id).orElse(null)
            ?: throw EntityNotFoundException("This id (${id}) does not exist")
        return result
    }
    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun updateState(id: Long, createMessageDTO: CMessageDTO, comment: String?): MessageDTO {
        val message = findById(id)
        message.state = createMessageDTO.state
        val history = History(CHistoryDTO(comment, createMessageDTO.state, Date()))
        message.addHistory(history)
        historyRepository.save(history)
        return messageRepository.save(message).toDto()
    }
    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun updatePriority(id: Long, priority: Priority): MessageDTO {
        val message = findById(id)
        message.priority = priority
        return messageRepository.save(message).toDto()
    }
}