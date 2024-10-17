package org.example.crm.integrationTest.controllers


import com.fasterxml.jackson.databind.ObjectMapper
import org.example.crm.dtos.request.create.CMessageDTO
import org.example.crm.dtos.request.update.UPriorityMessageDTO
import org.example.crm.dtos.request.update.UStateMessageDTO
import org.example.crm.utils.enums.Channel
import org.example.crm.utils.enums.Priority
import org.example.crm.utils.enums.State
import org.example.crm.utils.enums.Transition
import org.hamcrest.Matchers
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.util.*

@AutoConfigureMockMvc
@Sql("/sql/messageData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class MessageControllerIntegrationTest : IntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    val expect = listOf(
        JSONObject().apply {
            put("comment", "comment")
            put("body", "body")
            put("channel", "EMAIL")
            put("priority", "HIGH")
            put("sender", "alessia@live.com")
            put("state", "READ")
            put("subject", "subject")
        },
        JSONObject().apply {
            put("comment", "comment")
            put("body", "body")
            put("channel", "PHONE_CALL")
            put("priority", "LOW")
            put("sender", "1234567890")
            put("state", "RECEIVED")
            put("subject", "subject")
        })

    @Test
    fun listAll() {
        mockMvc.get("/api/messages") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.size()", Matchers.`is`(2)) }
        }
    }

    @Test
    fun listAllPaginationAndLimiting() {
        mockMvc.get("/api/messages/?pageNumber=1&limit=1") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.size()", Matchers.`is`(1)) }
            content { jsonPath("$[0].comment", Matchers.`is`(expect[0].getString("comment"))) }
            content { jsonPath("$[0].body", Matchers.`is`(expect[0].getString("body"))) }
            content { jsonPath("$[0].channel", Matchers.`is`(expect[0].getString("channel"))) }
            content { jsonPath("$[0].priority", Matchers.`is`(expect[0].getString("priority"))) }
            content { jsonPath("$[0].sender", Matchers.`is`(expect[0].getString("sender"))) }
            content { jsonPath("$[0].state", Matchers.`is`(expect[0].getString("state"))) }
            content { jsonPath("$[0].subject", Matchers.`is`(expect[0].getString("subject"))) }
        }
    }

    @Test
    fun listAllWithPaginationAndLimitingNegative() {
        mockMvc.get("/api/messages?pageNumber=-1&limit=-1") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun listAllWithSortingIdAsc() {
        mockMvc.get("/api/messages?sortingId=asc") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { (jsonPath("$.size()", Matchers.`is`(2))) }
            content { jsonPath("$[0].comment", Matchers.`is`(expect[0].getString("comment"))) }
            content { jsonPath("$[0].body", Matchers.`is`(expect[0].getString("body"))) }
            content { jsonPath("$[0].channel", Matchers.`is`(expect[0].getString("channel"))) }
            content { jsonPath("$[0].priority", Matchers.`is`(expect[0].getString("priority"))) }
            content { jsonPath("$[0].sender", Matchers.`is`(expect[0].getString("sender"))) }
            content { jsonPath("$[0].state", Matchers.`is`(expect[0].getString("state"))) }
            content { jsonPath("$[0].subject", Matchers.`is`(expect[0].getString("subject"))) }
            content { jsonPath("$[1].comment", Matchers.`is`(expect[1].getString("comment"))) }
            content { jsonPath("$[1].body", Matchers.`is`(expect[1].getString("body"))) }
            content { jsonPath("$[1].channel", Matchers.`is`(expect[1].getString("channel"))) }
            content { jsonPath("$[1].priority", Matchers.`is`(expect[1].getString("priority"))) }
            content { jsonPath("$[1].sender", Matchers.`is`(expect[1].getString("sender"))) }
            content { jsonPath("$[1].state", Matchers.`is`(expect[1].getString("state"))) }
            content { jsonPath("$[1].subject", Matchers.`is`(expect[1].getString("subject"))) }
        }
    }

    @Test
    fun listAllWithSortingIdDesc() {
        mockMvc.get("/api/messages?sortingId=desc") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { (jsonPath("$.size()", Matchers.`is`(2))) }
            content { jsonPath("$[0].comment", Matchers.`is`(expect[1].getString("comment"))) }
            content { jsonPath("$[0].body", Matchers.`is`(expect[1].getString("body"))) }
            content { jsonPath("$[0].channel", Matchers.`is`(expect[1].getString("channel"))) }
            content { jsonPath("$[0].priority", Matchers.`is`(expect[1].getString("priority"))) }
            content { jsonPath("$[0].sender", Matchers.`is`(expect[1].getString("sender"))) }
            content { jsonPath("$[0].state", Matchers.`is`(expect[1].getString("state"))) }
            content { jsonPath("$[0].subject", Matchers.`is`(expect[1].getString("subject"))) }
            content { jsonPath("$[1].comment", Matchers.`is`(expect[0].getString("comment"))) }
            content { jsonPath("$[1].body", Matchers.`is`(expect[0].getString("body"))) }
            content { jsonPath("$[1].channel", Matchers.`is`(expect[0].getString("channel"))) }
            content { jsonPath("$[1].priority", Matchers.`is`(expect[0].getString("priority"))) }
            content { jsonPath("$[1].sender", Matchers.`is`(expect[0].getString("sender"))) }
            content { jsonPath("$[1].state", Matchers.`is`(expect[0].getString("state"))) }
            content { jsonPath("$[1].subject", Matchers.`is`(expect[0].getString("subject"))) }
        }
    }

    @Test
    fun listAllWithSortingDateAsc() {
        mockMvc.get("/api/messages/?sortingDate=asc") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { (jsonPath("$.size()", Matchers.`is`(2))) }
            content { jsonPath("$[0].comment", Matchers.`is`(expect[0].getString("comment"))) }
            content { jsonPath("$[0].body", Matchers.`is`(expect[0].getString("body"))) }
            content { jsonPath("$[0].channel", Matchers.`is`(expect[0].getString("channel"))) }
            content { jsonPath("$[0].priority", Matchers.`is`(expect[0].getString("priority"))) }
            content { jsonPath("$[0].sender", Matchers.`is`(expect[0].getString("sender"))) }
            content { jsonPath("$[0].state", Matchers.`is`(expect[0].getString("state"))) }
            content { jsonPath("$[0].subject", Matchers.`is`(expect[0].getString("subject"))) }
            content { jsonPath("$[1].comment", Matchers.`is`(expect[1].getString("comment"))) }
            content { jsonPath("$[1].body", Matchers.`is`(expect[1].getString("body"))) }
            content { jsonPath("$[1].channel", Matchers.`is`(expect[1].getString("channel"))) }
            content { jsonPath("$[1].priority", Matchers.`is`(expect[1].getString("priority"))) }
            content { jsonPath("$[1].sender", Matchers.`is`(expect[1].getString("sender"))) }
            content { jsonPath("$[1].state", Matchers.`is`(expect[1].getString("state"))) }
            content { jsonPath("$[1].subject", Matchers.`is`(expect[1].getString("subject"))) }
        }
    }

    @Test
    fun listAllWithSortingDateDesc() {
        mockMvc.get("/api/messages/?sortingDate=desc") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { (jsonPath("$.size()", Matchers.`is`(2))) }
            content { jsonPath("$[0].comment", Matchers.`is`(expect[1].getString("comment"))) }
            content { jsonPath("$[0].body", Matchers.`is`(expect[1].getString("body"))) }
            content { jsonPath("$[0].channel", Matchers.`is`(expect[1].getString("channel"))) }
            content { jsonPath("$[0].priority", Matchers.`is`(expect[1].getString("priority"))) }
            content { jsonPath("$[0].sender", Matchers.`is`(expect[1].getString("sender"))) }
            content { jsonPath("$[0].state", Matchers.`is`(expect[1].getString("state"))) }
            content { jsonPath("$[0].subject", Matchers.`is`(expect[1].getString("subject"))) }
            content { jsonPath("$[1].comment", Matchers.`is`(expect[0].getString("comment"))) }
            content { jsonPath("$[1].body", Matchers.`is`(expect[0].getString("body"))) }
            content { jsonPath("$[1].channel", Matchers.`is`(expect[0].getString("channel"))) }
            content { jsonPath("$[1].priority", Matchers.`is`(expect[0].getString("priority"))) }
            content { jsonPath("$[1].sender", Matchers.`is`(expect[0].getString("sender"))) }
            content { jsonPath("$[1].state", Matchers.`is`(expect[0].getString("state"))) }
            content { jsonPath("$[1].subject", Matchers.`is`(expect[0].getString("subject"))) }
        }
    }

    @Test
    fun listAllWithPriorityLow() {
        mockMvc.get("/api/messages?priority=LOW") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { (jsonPath("$.size()", Matchers.`is`(1))) }
            content { jsonPath("$[0].comment", Matchers.`is`(expect[1].getString("comment"))) }
            content { jsonPath("$[0].body", Matchers.`is`(expect[1].getString("body"))) }
            content { jsonPath("$[0].channel", Matchers.`is`(expect[1].getString("channel"))) }
            content { jsonPath("$[0].priority", Matchers.`is`(expect[1].getString("priority"))) }
            content { jsonPath("$[0].sender", Matchers.`is`(expect[1].getString("sender"))) }
            content { jsonPath("$[0].state", Matchers.`is`(expect[1].getString("state"))) }
            content { jsonPath("$[0].subject", Matchers.`is`(expect[1].getString("subject"))) }
        }
    }

    @Test
    fun listAllWithStateRead() {
        mockMvc.get("/api/messages?state=READ") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { (jsonPath("$.size()", Matchers.`is`(2))) }
            content { jsonPath("$[0].comment", Matchers.`is`(expect[0].getString("comment"))) }
            content { jsonPath("$[0].body", Matchers.`is`(expect[0].getString("body"))) }
            content { jsonPath("$[0].channel", Matchers.`is`(expect[0].getString("channel"))) }
            content { jsonPath("$[0].priority", Matchers.`is`(expect[0].getString("priority"))) }
            content { jsonPath("$[0].sender", Matchers.`is`(expect[0].getString("sender"))) }
            content { jsonPath("$[0].state", Matchers.`is`(expect[0].getString("state"))) }
            content { jsonPath("$[0].subject", Matchers.`is`(expect[0].getString("subject"))) }
        }
    }

    @Test
    fun listAllWithChannelPhoneCall() {
        mockMvc.get("/api/messages?channel=PHONE_CALL") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { (jsonPath("$.size()", Matchers.`is`(1))) }
            content { jsonPath("$[0].comment", Matchers.`is`(expect[1].getString("comment"))) }
            content { jsonPath("$[0].body", Matchers.`is`(expect[1].getString("body"))) }
            content { jsonPath("$[0].channel", Matchers.`is`(expect[1].getString("channel"))) }
            content { jsonPath("$[0].priority", Matchers.`is`(expect[1].getString("priority"))) }
            content { jsonPath("$[0].sender", Matchers.`is`(expect[1].getString("sender"))) }
            content { jsonPath("$[0].state", Matchers.`is`(expect[1].getString("state"))) }
            content { jsonPath("$[0].subject", Matchers.`is`(expect[1].getString("subject"))) }
        }
    }

    @Test
    fun listAllWithMoreParameterZeroValue() {
        mockMvc.get("/api/messages/?pageNumber=1&limit=1&sortingId=asc&sortingDate=desc&priority=LOW&state=RECEIVED&channel=EMAIL") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { (jsonPath("$.size()", Matchers.`is`(0))) }
        }
    }

    @Test
    fun listAllWithMoreParameterOneValue() {
        mockMvc.get("/api/messages/?pageNumber=1&limit=1&sortingId=asc&sortingDate=desc&priority=HIGH&state=READ&channel=EMAIL") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { (jsonPath("$.size()", Matchers.`is`(1))) }
            content { jsonPath("$[0].comment", Matchers.`is`(expect[0].getString("comment"))) }
            content { jsonPath("$[0].body", Matchers.`is`(expect[0].getString("body"))) }
            content { jsonPath("$[0].channel", Matchers.`is`(expect[0].getString("channel"))) }
            content { jsonPath("$[0].priority", Matchers.`is`(expect[0].getString("priority"))) }
            content { jsonPath("$[0].sender", Matchers.`is`(expect[0].getString("sender"))) }
            content { jsonPath("$[0].state", Matchers.`is`(expect[0].getString("state"))) }
            content { jsonPath("$[0].subject", Matchers.`is`(expect[0].getString("subject"))) }
        }
    }

    @Test
    fun findByMessageIdExist() {
        mockMvc.get("/api/messages/1") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.id", Matchers.`is`(1)) }
            content { jsonPath("$.comment", Matchers.`is`(expect[0].getString("comment"))) }
            content { jsonPath("$.body", Matchers.`is`(expect[0].getString("body"))) }
            content { jsonPath("$.channel", Matchers.`is`(expect[0].getString("channel"))) }
            content { jsonPath("$.priority", Matchers.`is`(expect[0].getString("priority"))) }
            content { jsonPath("$.sender", Matchers.`is`(expect[0].getString("sender"))) }
            content { jsonPath("$.state", Matchers.`is`(expect[0].getString("state"))) }
            content { jsonPath("$.subject", Matchers.`is`(expect[0].getString("subject"))) }
        }
    }


    @Test
    fun findByMessageIdNotExist() {
        mockMvc.get("/api/messages/3") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun findHistoryByMessageIdExist() {
        val expect = listOf(
            JSONObject().apply {
                put("comment", "comment")
                put("state", "READ")
            },
            JSONObject().apply {
                put("state", "RECEIVED")
                put("comment", "comment")
            }

        )
        mockMvc.get("/api/messages/1/history") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.size()", Matchers.`is`(2)) }
            content { jsonPath("$[0].comment", Matchers.`is`(expect[0].getString("comment"))) }
            content { jsonPath("$[0].state", Matchers.`is`(expect[0].getString("state"))) }
            content { jsonPath("$[1].comment", Matchers.`is`(expect[1].getString("comment"))) }
            content { jsonPath("$[1].state", Matchers.`is`(expect[1].getString("state"))) }
        }
    }

    @Test
    fun findHistoryByMessageIdNotExist() {
        mockMvc.get("/api/messages/3/history") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `addMessage should return status CREATED`() {
        val messageDTO = CMessageDTO(
            sender = "1234567891",
            date = Date(),
            subject = "subject",
            body = "body",
            channel = Channel.TEXT_MESSAGE,
            priority = Priority.HIGH,
            state = State.RECEIVED,
            comment = "comment",
        )
        val messageJson = objectMapper.writeValueAsString(messageDTO)

        // Act and Assert
        mockMvc.post("/api/messages") {
            contentType = MediaType.APPLICATION_JSON
            content = messageJson
        }.andExpect {
            status { isCreated() }
            jsonPath("$.sender", Matchers.`is`(messageDTO.sender))
            jsonPath("$.subject", Matchers.`is`(messageDTO.subject))
            jsonPath("$.body", Matchers.`is`(messageDTO.body))
            jsonPath("$.channel", Matchers.`is`(messageDTO.channel.name))
            jsonPath("$.priority", Matchers.`is`(messageDTO.priority?.name))
            jsonPath("$.state", Matchers.`is`(messageDTO.state.name))
            jsonPath("$.comment", Matchers.`is`(messageDTO.comment))
        }
    }

    @Test
    fun `updateState should return status CREATED`() {
        // Arrange
        val messageId = 51L // sostituisci con un ID di messaggio valido
        val requestStateDTO = UStateMessageDTO(
            transition = Transition.READMESSAGE,
            comment = "Test comment"
        )
        val requestJson = objectMapper.writeValueAsString(requestStateDTO)

        mockMvc.post("/api/messages/$messageId") {
            contentType = MediaType.APPLICATION_JSON
            content = requestJson
        }.andExpect {
            status { isCreated() }
            jsonPath("$.state", Matchers.`is`(requestStateDTO.transition.getState().name))
        }
    }

    @Test
    fun `updateState should return status BADREQUEST, state not allowed`() {
        // Arrange
        val messageId = 51L // sostituisci con un ID di messaggio valido
        val requestStateDTO = UStateMessageDTO(
            transition = Transition.FAILMESSAGE,
            comment = "Test comment"
        )
        val requestJson = objectMapper.writeValueAsString(requestStateDTO)

        mockMvc.post("/api/messages/$messageId") {
            contentType = MediaType.APPLICATION_JSON
            content = requestJson
        }.andExpect {
            status { isBadRequest() }
        }
    }


    @Test
    fun `updateState should return status NOTFOUD, id does not exist`() {
        // Arrange
        val messageId = 2L // sostituisci con un ID di messaggio valido
        val requestStateDTO = UStateMessageDTO(
            transition = Transition.FAILMESSAGE,
            comment = "Test comment"
        )
        val requestJson = objectMapper.writeValueAsString(requestStateDTO)

        mockMvc.post("/api/messages/$messageId") {
            contentType = MediaType.APPLICATION_JSON
            content = requestJson
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `updatePriority should return status CREATED`() {
        // Arrange
        val messageId = 51L // sostituisci con un ID di messaggio valido
        val requestPriorityDTO = UPriorityMessageDTO(Priority.HIGH)
        val requestJson = objectMapper.writeValueAsString(requestPriorityDTO)

        mockMvc.put("/api/messages/$messageId/priority/") {
            contentType = MediaType.APPLICATION_JSON
            content = requestJson
        }.andExpect {
            status { isOk() }
            jsonPath("$.priority", Matchers.`is`(requestPriorityDTO.priority.toString()))
        }
    }


    @Test
    fun `updatePriority should return status NOTFOUD, id does not exist`() {
        // Arrange
        val messageId = 2L // sostituisci con un ID di messaggio valido
        val requestPriorityDTO = UPriorityMessageDTO(Priority.HIGH)
        val requestJson = objectMapper.writeValueAsString(requestPriorityDTO)

        mockMvc.put("/api/messages/$messageId/priority/") {
            contentType = MediaType.APPLICATION_JSON
            content = requestJson
        }.andExpect {
            status { isNotFound() }
        }
    }
}

