package org.example.analytics.dtos

import org.example.analytics.documents.Customer

data class CustomerDTO(
    val id: String?,
    val customerId: Long,
    val created: Long,
    val aborted: Long,
    val selection_phase: Long,
    val candidate_proposal: Long,
    val consolidated: Long,
    val done: Long,
)

fun Customer.toDTO(): CustomerDTO =
    CustomerDTO(
        this.id,
        this.customerId,
        this.created,
        this.aborted,
        this.selection_phase,
        this.candidate_proposal,
        this.consolidated,
        this.done,
    )

fun CustomerDTO.toResponse(): Map<String, Any?> =
    mapOf(
        "id" to this.id,
        "customerId" to this.customerId,
        "created" to this.created,
        "aborted" to this.aborted,
        "selection_phase" to this.selection_phase,
        "candidate_proposal" to this.candidate_proposal,
        "consolidated" to this.consolidated,
        "done" to this.done,
    )