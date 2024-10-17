package org.example.document_store.repositories

import org.example.document_store.entities.DocumentContent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DocumentContentRepository: JpaRepository<DocumentContent, Long> {

}