package org.example.crm.entities

import jakarta.persistence.*
import org.example.crm.dtos.request.create.CHistoryDTO
import org.example.crm.utils.enums.State
import java.util.*

@Entity
class History(dto: CHistoryDTO) : BaseEntity(dto.comment) {

    @Enumerated(EnumType.STRING)
    var state: State = dto.state

    @Temporal(TemporalType.TIMESTAMP)
    var date: Date = dto.date

    @ManyToOne
    var messages: Message? = null

}