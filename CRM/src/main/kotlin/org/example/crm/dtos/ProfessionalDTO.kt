package org.example.crm.dtos

import org.example.crm.entities.Professional
import org.example.crm.entities.Skill
import org.example.crm.utils.enums.EmploymentState

data class ProfessionalDTO(
    val id: Long,
    val skills: Set<Skill>,
    val dailyRate: Float,
    val employmentState: EmploymentState,
    val location: String?,
    val comment: String?,
    val jobOffers: List<Any>
) {
    fun toResponseEntity() = mapOf(
        "id" to id,
        "skills" to skills.map { it.toDTO() },
        "dailyRate" to dailyRate,
        "employmentState" to employmentState,
        "location" to location,
        "comment" to comment,
        "jobOffers" to jobOffers
    )
}

fun Professional.toDTO() = ProfessionalDTO(
    id = id,
    dailyRate = dailyRate,
    skills = skills,
    employmentState = employmentState,
    location = location,
    comment = comment,
    jobOffers = listOf() //jobOffers
)
