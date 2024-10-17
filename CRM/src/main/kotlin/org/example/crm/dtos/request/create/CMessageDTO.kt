package org.example.crm.dtos.request.create

import jakarta.validation.constraints.NotBlank
import org.example.crm.utils.enums.Channel
import org.example.crm.utils.enums.Priority
import org.example.crm.utils.enums.State
import org.example.crm.utils.isEmail
import org.example.crm.utils.isPhoneNumber
import java.util.*

data class CMessageDTO(
    @field:NotBlank(message = "Sender is required")
    val sender: String,
    val subject: String? = null,
    val date: Date = Date(),
    var state: State = State.RECEIVED,
    val channel: Channel,
    val body: String? = null,
    val priority: Priority? = null,
    val comment: String? = null
) {
    init {
        if (channel == Channel.PHONE_CALL || channel == Channel.TEXT_MESSAGE) {
            if (!isPhoneNumber(sender))
                throw IllegalArgumentException("The sender must be a phone number")
        } else {
            if (!isEmail(sender))
                throw IllegalArgumentException("The sender must be an email address")
        }
    }
}
