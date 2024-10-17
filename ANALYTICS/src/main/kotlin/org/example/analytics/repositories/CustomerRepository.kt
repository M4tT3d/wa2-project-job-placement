package org.example.analytics.repositories

import org.example.analytics.documents.Customer
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository : MongoRepository<Customer, String?> {

    fun findByCustomerId(customerId: Long): Customer?
}