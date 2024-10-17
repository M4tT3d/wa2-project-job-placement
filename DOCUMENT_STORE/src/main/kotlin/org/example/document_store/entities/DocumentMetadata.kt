package org.example.document_store.entities

import jakarta.persistence.*
import org.example.document_store.dtos.CreateDocumentMetadataDTO
import java.util.*

@Entity
class DocumentMetadata(dto: CreateDocumentMetadataDTO) {
    @Id
    @GeneratedValue
    var id: Long = 0

    @Column
    var name: String = dto.name
    var size: Long = dto.size
    var type: String = dto.type
    var contactId: Long = dto.contactId

    @Temporal(TemporalType.TIMESTAMP)
    var date: Date = dto.date

    @OneToOne(cascade = [CascadeType.ALL])
    var documentContent: DocumentContent = DocumentContent(dto.documentContentDTO.content)
}