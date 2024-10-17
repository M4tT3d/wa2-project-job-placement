package org.example.analytics.documents

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "professionals")
data class Professional(
    var employed: Long,
    var unemployed_available: Long,
    var not_available: Long,
    var rateAverage: Long,
) : BaseDocument()
