package org.example.document_store.dtos

import java.util.*

data class CreateDocumentMetadataDTO(
    val name: String,
    val size: Long,
    val type: String,
    val date: Date,
    val contactId: Long,
    val documentContentDTO: CreateDocumentContentDTO
)