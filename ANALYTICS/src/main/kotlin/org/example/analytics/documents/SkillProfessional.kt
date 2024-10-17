package org.example.analytics.documents

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "SkillProfessional")
data class SkillProfessional(
    val skillId: Long,
    var name: String,
    var rate: Long,
) : BaseDocument()
