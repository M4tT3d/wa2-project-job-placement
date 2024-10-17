package org.example.analytics.repositories

import org.example.analytics.documents.JobOffer
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface JobOfferRepository : MongoRepository<JobOffer, String?> {

    fun findByJobOfferId(jobOfferId: Long): JobOffer?
}