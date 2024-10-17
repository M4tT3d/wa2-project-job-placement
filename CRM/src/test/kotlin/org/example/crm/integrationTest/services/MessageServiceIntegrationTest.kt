package org.example.crm.integrationTest.services

import jakarta.persistence.EntityNotFoundException
import org.example.crm.dtos.request.create.CMessageDTO
import org.example.crm.dtos.request.filters.MessageParams
import org.example.crm.dtos.request.filters.PaginationParams
import org.example.crm.integrationTest.controllers.IntegrationTest
import org.example.crm.services.MessageService
import org.example.crm.utils.enums.Channel
import org.example.crm.utils.enums.Priority
import org.example.crm.utils.enums.State
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import java.text.SimpleDateFormat
import java.util.*


@SpringBootTest
@ContextConfiguration(initializers = [IntegrationTest.Initializer::class])
class MessageServiceIntegrationTest : IntegrationTest() {
    @Autowired
    private lateinit var messageService: MessageService

    @Test
    fun testCreateNewMessage() {
        // Arrange
        val createMessageDTO = CMessageDTO(
            sender = "1234567891",
            date = stringToDate("2022-01-01"),
            subject = "Test subject",
            body = "Test body",
            channel = Channel.TEXT_MESSAGE,
            priority = Priority.HIGH,
            state = State.RECEIVED,
            comment = "Test comment",
        )

        // Act
        val createdMessage = messageService.create(createMessageDTO)

        // Assert
        Assertions.assertNotNull(createdMessage)
        Assertions.assertEquals(createMessageDTO.sender, createdMessage.sender)
        Assertions.assertEquals(createMessageDTO.date, createdMessage.date)
        Assertions.assertEquals(createMessageDTO.subject, createdMessage.subject)
        Assertions.assertEquals(createMessageDTO.body, createdMessage.body)
        Assertions.assertEquals(createMessageDTO.channel, createdMessage.channel)
        Assertions.assertEquals(createMessageDTO.priority, createdMessage.priority)
        Assertions.assertEquals(createMessageDTO.state, createdMessage.state)
        Assertions.assertEquals(createMessageDTO.comment, createdMessage.comment)

    }

    @Test
    fun testUpdateMessageState() {
        // Arrange
        val createMessageDTO = CMessageDTO(
            sender = "1234567891",
            date = stringToDate("2022-01-01"),
            subject = "Test subject",
            body = "Test body",
            channel = Channel.TEXT_MESSAGE,
            priority = Priority.HIGH,
            state = State.RECEIVED,
            comment = "Test comment",
        )
        val createdMessage = messageService.create(createMessageDTO)
        val newState = State.READ

        // Act
        val updatedMessage =
            messageService.updateState(createdMessage.id, createMessageDTO.copy(state = newState), "State updated")

        // Assert
        Assertions.assertNotNull(updatedMessage)
        Assertions.assertEquals(newState, updatedMessage.state)
    }

    @Test
    fun testUpdateMessagePriority() {
        // Arrange
        val createMessageDTO = CMessageDTO(
            sender = "1234567891",
            date = stringToDate("2022-01-01"),
            subject = "Test subject",
            body = "Test body",
            channel = Channel.TEXT_MESSAGE,
            priority = Priority.HIGH,
            state = State.RECEIVED,
            comment = "Test comment",
        )
        val createdMessage = messageService.create(createMessageDTO)
        val newPriority = Priority.LOW

        // Act
        val updatedMessage = messageService.updatePriority(createdMessage.id, newPriority)

        // Assert
        Assertions.assertNotNull(updatedMessage)
        Assertions.assertEquals(newPriority, updatedMessage.priority)
    }

    @Test
    fun testListAllMessages() {
        // Arrange
        val createMessageDTO = CMessageDTO(
            sender = "1234567891",
            date = stringToDate("2022-01-01"),
            subject = "Test subject",
            body = "Test body",
            channel = Channel.TEXT_MESSAGE,
            priority = Priority.HIGH,
            state = State.RECEIVED,
            comment = "Test comment",
        )
        messageService.create(createMessageDTO)
        val paginationParams = PaginationParams()
        val params = MessageParams("asc", "asc")

        // Act
        val messages = messageService.listAll(paginationParams, params)

        // Assert
        Assertions.assertTrue(messages.isNotEmpty())
    }

    @Test
    fun testFindMessageById() {
        // Arrange
        val createMessageDTO = CMessageDTO(
            sender = "1234567891",
            date = stringToDate("2022-01-01"),
            subject = "Test subject",
            body = "Test body",
            channel = Channel.TEXT_MESSAGE,
            priority = Priority.HIGH,
            state = State.RECEIVED,
            comment = "Test comment",
        )
        val createdMessage = messageService.create(createMessageDTO)

        // Act
        val foundMessage = messageService.findById(createdMessage.id)

        // Assert
        Assertions.assertNotNull(foundMessage)
        Assertions.assertEquals(createdMessage.id, foundMessage.id)
    }

    @Test
    fun testFindMessageByNonExistentId() {
        // Arrange
        val nonExistentId = 9999L // Assumiamo che questo ID non esista nel database

        // Act and Assert
        assertThrows<EntityNotFoundException> {
            messageService.findById(nonExistentId)
        }
    }
}


fun stringToDate(dateString: String, format: String = "yyyy-MM-dd"): Date {
    val formatter = SimpleDateFormat(format)
    return formatter.parse(dateString)
}