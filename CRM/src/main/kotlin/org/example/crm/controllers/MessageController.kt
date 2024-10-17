package org.example.crm.controllers

import jakarta.validation.Valid
import org.example.crm.dtos.request.create.CMessageDTO
import org.example.crm.dtos.request.filters.MessageParams
import org.example.crm.dtos.request.filters.PaginationParams
import org.example.crm.dtos.request.update.UPriorityMessageDTO
import org.example.crm.dtos.request.update.UStateMessageDTO
import org.example.crm.dtos.toDto
import org.example.crm.dtos.toResponse
import org.example.crm.exceptions.InvalidStateException
import org.example.crm.services.ContactService
import org.example.crm.services.HistoryService
import org.example.crm.services.MessageService
import org.example.crm.utils.enums.Channel
import org.example.crm.utils.enums.State
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/messages")
class MessageController(
    private val messageService: MessageService,
    private val historyService: HistoryService,
    private val contactService: ContactService,
) {
    private val logger = LoggerFactory.getLogger(MessageController::class.java)

    @GetMapping("/", "")
    fun listAll(
        @Validated paginationParams: PaginationParams,
        @Validated params: MessageParams,
    ): List<Map<String, Any?>> {
        val response = messageService.listAll(
            paginationParams,
            params
        ).map { it.toResponse() }
        logger.info("Messages retrieved")
        return response
    }

    @PostMapping("/", "")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: CMessageDTO): Map<String, Any?> {
        logger.info("Starting to create a new document")
        request.state = State.RECEIVED
        val newMessage = messageService.create(
            request
        )
        val response: Boolean =
            if (request.channel == Channel.PHONE_CALL || request.channel == Channel.TEXT_MESSAGE) {
                contactService.newPhoneNumber(request.sender)
            } else {
                contactService.newEmail(request.sender)
            }
        if (!response) logger.info("\n The sender of the message is unknown, the contact has been saved as unknown Contact(?,?) \n")


        logger.info(
            "\n id: ${newMessage.id}\n sender: ${newMessage.sender}\n date: ${newMessage.date}\n " +
                    "subject: ${newMessage.subject}\n body: ${newMessage.body}\n " +
                    "channel: ${newMessage.channel}\n priority: ${newMessage.priority}\n " +
                    "state: ${newMessage.state}\n comment: ${newMessage.comment}\n"
        )
        return newMessage.toResponse()

    }


    @PostMapping("/{messageId}/", "/{messageId}")
    @ResponseStatus(HttpStatus.CREATED)
    fun updateState(
        @RequestBody request: UStateMessageDTO,
        @PathVariable messageId: Long
    ): ResponseEntity<Map<String, Any?>> {

        logger.info("Change state of message")

        val message = messageService.findById(messageId)
        val listTransition = message.state.list


        val newTransition = request.transition
        if (listTransition.contains(newTransition)) {
            val newMessage = CMessageDTO(
                sender = message.sender,
                date = message.date,
                subject = message.subject,
                body = message.body,
                channel = message.channel,
                state = newTransition.getState(),
                priority = message.priority,
                comment = message.comment,
            )

            val response = messageService.updateState(messageId, newMessage, request.comment)
            logger.info(
                "the state of the message with the id: ${message.id} has been updated \n"
            )

            return ResponseEntity(
                response.toResponse(), HttpStatus.CREATED
            )

        } else {
            logger.info(
                "\nthe new state for the message with the id: ${message.id} is not allowed \n"
            )
            throw InvalidStateException("The state of the message could not be changed to the one requested, it is not allowed for this message (${message.id})")
        }

    }


    @GetMapping("/{messageId}/history", "/{messageId}/history/")
    fun findHistoryByMessageId(
        @RequestParam(defaultValue = "1") pagination: Int,
        @RequestParam(defaultValue = "10") limiting: Int,
        @PathVariable messageId: String
    ): List<Map<String, Any?>> {
        if (pagination < 1) throw IllegalArgumentException("The page number must be a positive number greater or equal to 1")
        if (limiting < 1) throw IllegalArgumentException("The limit must be a positive number greater or equal to 1")
        val history = historyService.findHistoryByMessageId(pagination - 1, limiting, messageId.toLong())
        logger.info("History of ${messageId.toLong()} retrieved")
        return history.map { it.toResponse() }
    }


    @PutMapping("/{messageId}/priority/", "/{messageId}/priority")
    @ResponseStatus(HttpStatus.OK)
    fun updatePriority(
        @RequestBody request: UPriorityMessageDTO,
        @PathVariable messageId: Long
    ): ResponseEntity<Map<String, Any?>> {
        logger.info("Change priority of message")

        val message = messageService.findById(messageId)
        val newMessage = messageService.updatePriority(messageId, request.priority)
        logger.info(
            "the priority of the message with the id: ${message.id} has been updated \n"
        )
        return ResponseEntity(
            newMessage.toResponse(), HttpStatus.OK
        )

    }


    @GetMapping("/{messageId}", "{messageId}")
    fun findByMessageId(
        @PathVariable messageId: String
    ): Map<String, Any?> {
        val message = messageService.findById(messageId.toLong())
        logger.info("Message ${messageId.toLong()} retrieved")
        return message.toDto().toResponse()
    }

}
