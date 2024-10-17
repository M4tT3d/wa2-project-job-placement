package org.example.crm.repositories

import org.example.crm.entities.Email
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailRepository : JpaRepository<Email, Long> {
    fun findByEmail(email: String): List<Email>
    fun findByContactId(contactId: Long): List<Email>
}