package org.example.analytics.documents

data class JobOffer(
    val jobOfferId: Long,
    var value: Float,
    var duration: Float
) : BaseDocument()
