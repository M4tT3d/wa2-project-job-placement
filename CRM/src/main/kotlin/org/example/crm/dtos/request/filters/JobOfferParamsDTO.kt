package org.example.crm.dtos.request.filters

import jakarta.validation.constraints.Min
import org.example.crm.entities.JobOffer
import org.example.crm.repositories.specifications.JobOfferSpecs
import org.example.crm.utils.enums.StateJobOffer
import org.example.crm.validators.JobOfferParams
import org.example.crm.validators.NullAndNotEmpty
import org.springframework.data.jpa.domain.Specification

@JobOfferParams(message = "Invalid params")
data class JobOfferParamsDTO(
    @field:Min(0)
    val customerId: Long? = null,
    @field:Min(0)
    val professionalId: Long? = null,
    @field:NullAndNotEmpty()
    val jobOffer: String? = null,
    val skills: Set<Long>? = null
){
    fun toSpecification(): Specification<JobOffer>{
        return when (jobOffer){
            "OPEN" -> {
                Specification.where<JobOffer>(null)
                    .and(customerId.let { JobOfferSpecs.customerIdEqual(customerId!!) })
                    .and(jobOffer.let { JobOfferSpecs.filterStateJobOfferNotEqual(StateJobOffer.ABORTED) })
                    .and(jobOffer.let { JobOfferSpecs.filterStateJobOfferNotEqual(StateJobOffer.CONSOLIDATED) })
                    .and(jobOffer.let { JobOfferSpecs.filterStateJobOfferNotEqual(StateJobOffer.DONE) })
            }

            "ACCEPTED" -> {
                Specification.where<JobOffer>(null)
                    .and(professionalId.let { JobOfferSpecs.professionalIdEqual(professionalId!!) })
                    .and((jobOffer.let { JobOfferSpecs.filterStateJobOfferEqual(StateJobOffer.CONSOLIDATED) })
                        .or(jobOffer.let { JobOfferSpecs.filterStateJobOfferEqual(StateJobOffer.DONE) })
                    )
            }

            else -> {
                Specification.where<JobOffer>(null)
                    .and(customerId?.let { JobOfferSpecs.customerIdEqual(customerId) })
                    .and(professionalId?.let { JobOfferSpecs.professionalIdEqual(professionalId) })
                    .and(jobOffer?.let { JobOfferSpecs.filterStateJobOfferEqual(StateJobOffer.valueOf(jobOffer.uppercase())) })
                    .and(skills?.let { JobOfferSpecs.filterSkillsContain(skills) })
            }
        }
    }
}


