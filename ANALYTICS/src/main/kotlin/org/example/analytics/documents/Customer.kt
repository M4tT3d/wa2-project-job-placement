package org.example.analytics.documents

data class Customer(
    val customerId: Long,
    var created: Long,
    var aborted: Long,
    var selection_phase: Long,
    var candidate_proposal: Long,
    var consolidated: Long,
    var done: Long,
) : BaseDocument()
