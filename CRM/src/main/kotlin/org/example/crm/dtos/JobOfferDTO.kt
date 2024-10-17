package org.example.crm.dtos

import org.example.crm.entities.Customer
import org.example.crm.entities.JobOffer
import org.example.crm.entities.Professional
import org.example.crm.entities.Skill
import org.example.crm.utils.enums.StateJobOffer

data class JobOfferDTO(
    val id: Long,
    val description: String?,
    val status: StateJobOffer,
    val skills: Set<Skill>,
    val comment: String?,
    val duration: Float,
    val customer: Customer,
    val professional: Professional? = null,
    val value: Float = 0.0f,
)

fun JobOffer.toDto(): JobOfferDTO =
    JobOfferDTO(
        this.id,
        this.description,
        this.status,
        this.skills,
        this.comment,
        this.duration,
        this.customer,
        this.professional,
        this.value,
    )

fun JobOfferDTO.toResponse(): Map<String, Any?> =
    mapOf(
        "id" to this.id,
        "description" to this.description,
        "status" to this.status,
        "skills" to this.skills.map { it.skill },
        "comment" to this.comment,
        "duration" to this.duration,
        "customerId" to this.customer.id,
        "professionalId" to this.professional?.id,
        "value" to this.value,
    )