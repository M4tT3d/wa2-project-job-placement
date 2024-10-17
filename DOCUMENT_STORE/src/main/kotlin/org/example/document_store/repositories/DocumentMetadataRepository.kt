package org.example.document_store.repositories

import org.example.document_store.entities.DocumentMetadata
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DocumentMetadataRepository:JpaRepository<DocumentMetadata,Long> {
    fun findByName(name: String): DocumentMetadata?
    fun findByContactId(contactId: Long): List<DocumentMetadata>?
}