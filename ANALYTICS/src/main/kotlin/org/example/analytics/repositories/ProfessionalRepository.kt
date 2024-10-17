package org.example.analytics.repositories

import org.example.analytics.documents.Professional
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ProfessionalRepository : MongoRepository<Professional, String?> {
}