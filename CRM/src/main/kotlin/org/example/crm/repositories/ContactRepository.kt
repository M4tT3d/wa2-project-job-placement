package org.example.crm.repositories

import org.example.crm.entities.Contact
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ContactRepository : JpaRepository<Contact, Long>, JpaSpecificationExecutor<Contact> {
    override fun findById(id: Long): Optional<Contact>
}