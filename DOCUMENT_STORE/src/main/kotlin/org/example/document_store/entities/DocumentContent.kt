package org.example.document_store.entities

import jakarta.persistence.*

@Entity
class DocumentContent(
    @Lob
    var content: ByteArray,
) {
    @Id
    @GeneratedValue
    var id: Long = 0

    @OneToOne(mappedBy = "documentContent")
    lateinit var metadata: DocumentMetadata
}