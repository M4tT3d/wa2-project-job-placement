package org.example.crm.repositories


import org.example.crm.entities.Address
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AddressRepository:JpaRepository<Address, Long> {
    fun findByContactId(contactId: Long): List<Address>
}