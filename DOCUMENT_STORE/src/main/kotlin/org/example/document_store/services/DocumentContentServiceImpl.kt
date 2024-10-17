package org.example.document_store.services

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.example.document_store.dtos.CreateDocumentMetadataDTO
import org.example.document_store.dtos.DocumentContentDTO
import org.springframework.security.access.prepost.PreAuthorize

import org.example.document_store.dtos.toDto


import org.example.document_store.repositories.DocumentContentRepository
import org.example.document_store.repositories.DocumentMetadataRepository
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity

import org.springframework.stereotype.Service


@Service
@Transactional
@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
class DocumentContentServiceImpl(
    private val documentContentRepository: DocumentContentRepository,
    private val documentMetadataRepository: DocumentMetadataRepository
) :
    DocumentContentService {

    override fun findById(id: Long): DocumentContentDTO {
        if (!documentContentRepository.findById(id).isPresent) {
            throw EntityNotFoundException("This id (${id}) does not exist")
        }
        return documentContentRepository.findById(id).map { it.toDto() }.get()
    }
    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun update(createDocumentMetadataDTO: CreateDocumentMetadataDTO, id: Long): DocumentContentDTO {
        val idContent = documentMetadataRepository.findById(id).get().documentContent.id

        val newDocumentContent = documentContentRepository.findById(idContent).get()
        newDocumentContent.content = createDocumentMetadataDTO.documentContentDTO.content
//        val newDocumentContent = DocumentContent(createDocumentMetadataDTO.documentContentDTO.content)
//        newDocumentContent.id = idContent
        return documentContentRepository.save(newDocumentContent).toDto()
    }

}