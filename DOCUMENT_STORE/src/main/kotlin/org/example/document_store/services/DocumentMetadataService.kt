package org.example.document_store.services

import org.example.document_store.dtos.CreateDocumentMetadataDTO

import org.example.document_store.dtos.DocumentMetadataDTO
import java.util.*

interface DocumentMetadataService {
    fun listAll(pageNumber: Int,limit:Int): List<DocumentMetadataDTO>
    fun findByContactId(contactId: Long): List<DocumentMetadataDTO>
    fun create(createDocumentMetadataDTO: CreateDocumentMetadataDTO): DocumentMetadataDTO
    fun findById(id: Long): DocumentMetadataDTO
    fun delete(id: Long)
    fun update(createDocumentMetadataDTO: CreateDocumentMetadataDTO, id: Long): DocumentMetadataDTO
}