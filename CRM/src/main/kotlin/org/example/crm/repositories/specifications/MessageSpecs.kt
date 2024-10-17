package org.example.crm.repositories.specifications

import org.example.crm.entities.Message
import org.example.crm.utils.enums.Channel
import org.example.crm.utils.enums.Priority
import org.example.crm.utils.enums.State
import org.springframework.data.jpa.domain.Specification

object MessageSpecs {

    fun stateEqual(state: State): Specification<Message> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.equal(root.get<State>("state"), state)
        }
    }

    fun priorityEqual(priority: Priority): Specification<Message> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.equal(root.get<Priority>("priority"), priority)
        }
    }

    fun channelEqual(channel: Channel): Specification<Message> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.equal(root.get<Channel>("channel"), channel)
        }
    }

}