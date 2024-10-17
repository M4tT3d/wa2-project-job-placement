package org.example.crm.entities

import jakarta.persistence.*
import org.example.crm.dtos.request.create.CJobOfferDTO
import org.example.crm.utils.enums.StateJobOffer

@Entity
class JobOffer(
    dto: CJobOfferDTO,
    @ManyToMany(cascade = [CascadeType.DETACH])
    @JoinTable(
        name = "job_offer_skills",
        joinColumns = [JoinColumn(name = "job_offer_id")],
        inverseJoinColumns = [JoinColumn(name = "skill_id")]
    )
    var skills: MutableSet<Skill>,

    @ManyToOne var customer: Customer
) : BaseEntity(dto.comment) {

    var description: String? = dto.description

    @Enumerated(EnumType.STRING)
    var status: StateJobOffer = StateJobOffer.CREATED


    var duration: Float = dto.duration

    var value: Float = 0.0f

    @ManyToOne
    var professional: Professional? = null


}