package org.example.crm.entities

import jakarta.persistence.*
import org.example.crm.dtos.request.create.CMessageDTO
import org.example.crm.utils.enums.Channel
import org.example.crm.utils.enums.Priority
import org.example.crm.utils.enums.State
import java.util.*

@Entity
class Message(dto: CMessageDTO) : BaseEntity(dto.comment) {
    var sender: String = dto.sender

    @Temporal(TemporalType.TIMESTAMP)
    var date: Date = dto.date

    var subject: String? = dto.subject

    @Column(columnDefinition = "TEXT")
    var body: String? = dto.body

    @Enumerated(EnumType.STRING)
    var channel: Channel = dto.channel

    @Enumerated(EnumType.STRING)
    var priority: Priority? = dto.priority

    @Enumerated(EnumType.STRING)
    var state: State = dto.state

    @OneToMany(mappedBy = "messages")
    var histories: MutableList<History> = mutableListOf()

    fun addHistory(h: History) {
        h.messages = this
        histories.add(h)
    }
}