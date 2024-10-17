package org.example.crm.repositories

import org.example.crm.entities.JobOffer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface JobOfferRepository : JpaRepository<JobOffer, Long>, JpaSpecificationExecutor<JobOffer>{
    fun findByCustomerId(customerId: Long): List<JobOffer>
    fun findByProfessionalId(professionalId: Long): List<JobOffer>
}