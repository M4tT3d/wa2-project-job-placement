package org.example.analytics.repositories

import org.example.analytics.documents.SkillJobOffer
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SkillJobOfferRepository : MongoRepository<SkillJobOffer, String?> {

    fun findBySkillId(skillId: Long): SkillJobOffer?
}