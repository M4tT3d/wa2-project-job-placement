package org.example.document_store.dtos

import org.example.document_store.entities.DocumentMetadata
import java.util.*

data class DocumentMetadataDTO(
    val id: Long,
    val name: String,
    val size: Long,
    val type: String,
    val date: Date,
    val contactId: Long,
    val documentContentDTO: DocumentContentDTO
)

fun DocumentMetadata.toDto(): DocumentMetadataDTO =
    DocumentMetadataDTO(this.id, this.name, this.size, this.type, this.date, this.contactId, this.documentContent.toDto())

