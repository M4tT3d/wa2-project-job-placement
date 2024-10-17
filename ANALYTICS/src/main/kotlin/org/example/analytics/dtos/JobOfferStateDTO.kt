package org.example.analytics.dtos

data class JobOfferStateDTO(
    val created: Long,
    val aborted: Long,
    val selection_phase: Long,
    val candidate_proposal: Long,
    val consolidated: Long,
    val done: Long,
)

fun JobOfferStateDTO.toResponse(): List<Map<String, Any?>> =
    listOf(
        mapOf("state" to "created", "value" to this.created),
        mapOf("state" to "aborted", "value" to this.aborted),
        mapOf("state" to "selection_phase", "value" to this.selection_phase),
        mapOf("state" to "candidate_proposal", "value" to this.candidate_proposal),
        mapOf("state" to "consolidated", "value" to this.consolidated),
        mapOf("state" to "done", "value" to this.done)

    )