package org.example.analytics.repositories

import org.example.analytics.documents.SkillProfessional
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SkillProfessionalRepository : MongoRepository<SkillProfessional, String?> {

    fun findBySkillId(skillId: Long): SkillProfessional?
}