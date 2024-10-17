package org.example.document_store.services

import org.example.document_store.dtos.CreateDocumentMetadataDTO
import org.example.document_store.dtos.DocumentContentDTO

interface DocumentContentService {
    fun findById(id: Long): DocumentContentDTO
    fun update(createDocumentMetadataDTO: CreateDocumentMetadataDTO, id: Long): DocumentContentDTO
}