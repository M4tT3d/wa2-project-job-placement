package org.example.crm.repositories

import org.example.crm.entities.Telephone
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TelephoneRepository : JpaRepository<Telephone, Long> {
    fun findByTelephone(telephone: String): List<Telephone>
    fun findByContactId(contactId: Long): List<Telephone>
}