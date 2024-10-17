package org.example.document_store.services

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.example.document_store.dtos.CreateDocumentMetadataDTO
import org.example.document_store.dtos.DocumentMetadataDTO
import org.example.document_store.dtos.toDto
import org.example.document_store.entities.DocumentMetadata
import org.example.document_store.repositories.DocumentMetadataRepository
import org.springframework.context.annotation.Configuration
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.stereotype.Service
import java.util.*

@Service
@Transactional
@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
class DocumentMetadataServiceImpl(
    val repo: DocumentMetadataRepository,
    private val documentMetadataRepository: DocumentMetadataRepository
) : DocumentMetadataService {

    override fun listAll(pageNumber: Int, limit: Int): List<DocumentMetadataDTO> {
        val pageable = PageRequest.of(pageNumber, limit)
        return documentMetadataRepository.findAll(pageable).content.map { it.toDto() }
    }
    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun create(createDocumentMetadataDTO: CreateDocumentMetadataDTO): DocumentMetadataDTO {
        val docMetadataEntity = DocumentMetadata(createDocumentMetadataDTO)
        return documentMetadataRepository.save(docMetadataEntity).toDto()
    }

    override fun findByContactId(contactId: Long): List<DocumentMetadataDTO> {
        if (documentMetadataRepository.findByContactId(contactId) == null)
            throw EntityNotFoundException("This contact id (${contactId}) does not have any documents")
        return documentMetadataRepository.findByContactId(contactId)!!.map { it.toDto() }
    }

    override fun findById(id: Long): DocumentMetadataDTO {
        if (!documentMetadataRepository.findById(id).isPresent) {
            throw EntityNotFoundException("This id (${id}) does not exist")
        }
        return documentMetadataRepository.findById(id).map { it.toDto() }.get()
    }
    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun delete(id: Long) {
        documentMetadataRepository.deleteById(id)
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun update(createDocumentMetadataDTO: CreateDocumentMetadataDTO, id: Long): DocumentMetadataDTO {
        val doc = repo.findById(id).get()

        doc.name = createDocumentMetadataDTO.name
        doc.size = createDocumentMetadataDTO.size
        doc.type = createDocumentMetadataDTO.type
        //doc.date = createDocumentMetadataDTO.date

        return repo.save(doc).toDto()
    }

}