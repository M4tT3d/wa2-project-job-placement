package org.example.crm.entities

import jakarta.persistence.*

@Entity
@Table(name = "skill")
@SequenceGenerator(
    name = "skill_seq",
    sequenceName = "skill_seq",
    allocationSize = 1
)
class Skill(
    @Column(nullable = false) var skill: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "skill_seq")
    val id: Long = 0

    @ManyToMany(mappedBy = "skills")
    val jobOffers: MutableSet<JobOffer> = mutableSetOf()

    @ManyToMany(mappedBy = "skills")
    val professionals: MutableSet<Professional> = mutableSetOf()

}