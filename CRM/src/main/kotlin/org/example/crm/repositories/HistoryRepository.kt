package org.example.crm.repositories

import org.example.crm.entities.History
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HistoryRepository : JpaRepository<History, Long> {
    fun findByMessages_Id(page: PageRequest, messageId: Long): List<History>
}