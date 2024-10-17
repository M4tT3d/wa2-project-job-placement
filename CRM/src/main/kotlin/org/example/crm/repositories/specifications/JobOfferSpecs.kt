package org.example.crm.repositories.specifications

import org.example.crm.entities.JobOffer
import org.example.crm.utils.enums.StateJobOffer
import org.springframework.data.jpa.domain.Specification


object JobOfferSpecs {

    fun customerIdEqual(customerId: Long): Specification<JobOffer> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.equal(root.get<Long>("customer").get<Long>("id"), customerId)
        }
    }

    fun professionalIdEqual(professionalId: Long): Specification<JobOffer> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.equal(root.get<Long>("professional").get<Long>("id"), professionalId)
        }
    }

    fun filterStateJobOfferEqual(stateJobOffer: StateJobOffer): Specification<JobOffer> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.equal(root.get<StateJobOffer>("status"), stateJobOffer)
        }
    }

    fun filterStateJobOfferNotEqual(stateJobOffer: StateJobOffer): Specification<JobOffer> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.notEqual(root.get<StateJobOffer>("status"), stateJobOffer)
        }
    }

    fun filterSkillsContain(skills: Set<Long>): Specification<JobOffer>{
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.and(
                *skills.map { skill ->
                    criteriaBuilder.equal(root.join<Set<Long>, Long>("skills").get<Long>("id"), skill)
                }.toTypedArray()
            )
        }
    }
}