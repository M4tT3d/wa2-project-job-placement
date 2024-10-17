package org.example.crm.dtos.request.filters

import jakarta.validation.constraints.Pattern
import org.example.crm.entities.Message
import org.example.crm.repositories.specifications.MessageSpecs
import org.example.crm.utils.enums.Channel
import org.example.crm.utils.enums.Priority
import org.example.crm.utils.enums.State
import org.example.crm.validators.NullAndNotEmpty
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification

data class MessageParams(
    @field:NullAndNotEmpty(message = "sortingId cannot be empty")
    @field:Pattern(regexp = "(asc|desc)", message = "sortingId must be 'asc' or 'desc'")
    val sortingId: String? = null,
    @field:NullAndNotEmpty(message = "sortingDate cannot be empty")
    @field:Pattern(regexp = "(asc|desc)", message = "sortingId must be 'asc' or 'desc'")
    val sortingDate: String? = null,
    val priority: Priority? = null,
    val channel: Channel? = null,
    val state: State? = null
) {
    fun toSpecification(): Specification<Message> {
        return Specification.where<Message>(null)
            .and(priority?.let { MessageSpecs.priorityEqual(it) })
            .and(channel?.let { MessageSpecs.channelEqual(it) })
            .and(state?.let { MessageSpecs.stateEqual(it) })
    }

    fun toSorting(): Sort {
        val orders = mutableListOf<Sort.Order>()
        if (sortingId == "desc") orders.add(Sort.Order.desc("id"))
        else if (sortingId == "asc") orders.add(Sort.Order.asc("id"))
        if (sortingDate == "desc") orders.add(Sort.Order.desc("date"))
        else if (sortingDate == "asc") orders.add(Sort.Order.asc("date"))
        return Sort.by(*orders.toTypedArray())
    }
}
