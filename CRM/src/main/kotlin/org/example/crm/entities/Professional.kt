package org.example.crm.entities

import jakarta.persistence.*
import org.example.crm.dtos.request.create.CProfessionalDTO
import org.example.crm.utils.enums.EmploymentState

@Entity
class Professional(
    @Column(nullable = false)
    var dailyRate: Float
) : BaseEntity() {

    @ManyToMany(cascade = [CascadeType.DETACH])
    @JoinTable(
        name = "professional_skills",
        joinColumns = [JoinColumn(name = "professional_id")],
        inverseJoinColumns = [JoinColumn(name = "skill_id")]
    )
    var skills: MutableSet<Skill> = mutableSetOf()

    @Enumerated(EnumType.STRING)
    var employmentState = EmploymentState.UNEMPLOYED_AVAILABLE

    var location: String? = null

    @OneToOne(mappedBy = "professional")
    lateinit var contact: Contact

    @OneToMany(mappedBy = "professional")
    var jobOffers: MutableList<JobOffer>? = mutableListOf()


    constructor(
        professionalDTO: CProfessionalDTO,
        contact: Contact,
        skills: Set<Skill>
    ) : this(
        professionalDTO.dailyRate
    ) {
        this.contact = contact
        this.employmentState = professionalDTO.employmentState
        this.location = professionalDTO.location
        this.skills = skills.toMutableSet()
    }
}