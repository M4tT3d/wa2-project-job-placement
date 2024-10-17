package org.example.document_store.controllers


import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.document_store.dtos.CreateDocumentContentDTO
import org.example.document_store.dtos.CreateDocumentMetadataDTO
import org.example.document_store.services.DocumentContentService
import org.example.document_store.services.DocumentMetadataService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*
import java.util.stream.Collectors

@RestController
@RequestMapping("/api/documents")
class DocumentController(
    private val documentMetadataService: DocumentMetadataService,
    private val documentContentService: DocumentContentService,
    private val response: HttpServletResponse
) {
    private val logger = LoggerFactory.getLogger(DocumentController::class.java)

    @GetMapping("/", "")
    fun listAll(
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "50") limit: Int
    ): List<Map<String, Any>> {
        var pageN = pageNumber
        var lim = limit
        if (pageN < 0) pageN = 0
        if (lim < 0) lim = 50
        logger.info("Listing all documents")
        return documentMetadataService.listAll(pageN, lim).map { doc ->
            mapOf(
                "metadataId" to doc.id,
                "name" to doc.name,
                "size" to doc.size,
                "type" to doc.type,
                "date" to doc.date,
                "documentId" to doc.documentContentDTO.id
            )
        }
    }

    @GetMapping("/contacts/{contactId}", "/contacts/{contactId}")
    fun findByContactId(@PathVariable contactId: String): List<Map<String, Any>> {
        return documentMetadataService.findByContactId(contactId.toLong()).map { doc ->
            mapOf(
                "metadataId" to doc.id,
                "name" to doc.name,
                "size" to doc.size,
                "type" to doc.type,
                "date" to doc.date,
                "documentId" to doc.documentContentDTO.id
            )
        }
    }

    @GetMapping("/{metadataId}", "{metadataId}")
    fun findByMetadataIdInfo(@PathVariable metadataId: String): Map<String, Any> {
        val doc = documentMetadataService.findById(metadataId.toLong())
        response.setHeader("Content-Type", doc.type)
        response.setHeader("name", doc.name)
        logger.info("Document ${metadataId.toLong()} info retrieved")
        return mapOf(
            "metadataId" to doc.id,
            "name" to doc.name,
            "size" to doc.size,
            "type" to doc.type,
            "date" to doc.date,
            "documentId" to doc.documentContentDTO.id
        )
    }

    @GetMapping("/{metadataId}/data/", "{metadataId}/data")
    fun findByMetadataIdContent(@PathVariable metadataId: String): ByteArray {
        val doc = documentMetadataService.findById(metadataId.toLong())
        response.setHeader("Content-Type", doc.type)
        response.setHeader("name", doc.name)
        logger.info("Document ${metadataId.toLong()} content retrieved")
        return doc.documentContentDTO.content
    }


    @PostMapping(
        "/",
        "",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        request: HttpServletRequest,
        @RequestParam("file") file: MultipartFile,
        @RequestParam("contact_id") contactId: String
    ): Map<String, Any> {
        logger.info("file: $file")
        logger.info("contact_id: $contactId")
        if (file == null) {
            throw IllegalArgumentException("File is required")
        } else {
            logger.info(
                "\nDocument data: \n\tname: ${file.originalFilename}\n\t" +
                        "size: ${file.size}\n\ttype: ${file.contentType}\n\tcontent: ${file.bytes}"
            )

            logger.info("Starting to create a new document")
            val newDoc = documentMetadataService.create(
                CreateDocumentMetadataDTO(
                    name = file.originalFilename ?: "",
                    size = file.size,
                    type = file.contentType ?: "",
                    date = Date(),
                    contactId = contactId.toLong(),
                    documentContentDTO = CreateDocumentContentDTO(file.bytes.clone())
                )
            )
            //log the request body at INFO level
            logger.info(
                "\nDocument data: \n\tname: ${newDoc.name}\n\t" +
                        "size: ${newDoc.size}\n\ttype: ${newDoc.type}\n\tdate: ${newDoc.date}"
            )
            return mapOf(
                "name" to newDoc.name,
                "size" to newDoc.size,
                "type" to newDoc.type,
                "date" to newDoc.date,
                "contact_id" to newDoc.contactId
            )
        }
    }

    @PutMapping(
        "/{metadataId}/",
        "/{metadataId}",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.OK)
    fun update(
        @RequestPart("file") file: MultipartFile,
        @PathVariable metadataId: String,
        @RequestParam("contact_id") contactId: String
    ): ResponseEntity<Map<String, Any?>> {
        logger.info("\nStarting to update the document\n")
        try {
            documentMetadataService.findById(metadataId.toLong())
            val newCreateDoc = CreateDocumentMetadataDTO(
                name = file.originalFilename ?: "",
                size = file.size,
                type = file.contentType ?: "",
                date = Date(),
                contactId = contactId.toLong(),
                documentContentDTO = CreateDocumentContentDTO(file.bytes.clone())
            )
            val newDoc = documentMetadataService.update(
                newCreateDoc, metadataId.toLong()
            )
            documentContentService.update(
                newCreateDoc, metadataId.toLong()
            )


            //log the request body at INFO level
            logger.info(
                "\nUpdated document: \n\tname: ${newDoc.name}\n\t" +
                        "size: ${newDoc.size}\n\ttype: ${newDoc.type}\n\t"
            )
            return ResponseEntity(
                mapOf(
                    "name" to newDoc.name,
                    "size" to newDoc.size,
                    "type" to newDoc.type,
                    "date" to newDoc.date,
                ), HttpStatus.OK
            )
        } catch (e: Exception) {
            logger.info("\nUpdate failed\n${e.message}\n")
            return ResponseEntity(mapOf("error" to e.message), HttpStatus.BAD_REQUEST)
        }

    }

    @DeleteMapping("/{metadataId}/", "/{metadataId}")
    fun deleteDocument(@PathVariable metadataId: Long): ResponseEntity<Any> {
        logger.info("\nStarting to delete a document\n")
        try {
            val checkM = documentMetadataService.findById(metadataId)
            documentMetadataService.delete(metadataId)
            logger.info(
                "\nDeleted success: \n\tname: ${checkM.name}\n\t" +
                        "size: ${checkM.size}\n\ttype: ${checkM.type}\n\tdate: ${checkM.date}"
            )
            return ResponseEntity(mapOf("response" to "$metadataId deleted with success"), HttpStatus.OK)
        } catch (e: Exception) {
            logger.info("\nDeleted failed\n${e.message}\n")
            return ResponseEntity(mapOf("error" to e.message), HttpStatus.BAD_REQUEST)
        }
    }

    @DeleteMapping("/contacts/{contactId}/", "/contacts/{contactId}")
    fun deleteDocumentByContactId(@PathVariable contactId: Long): ResponseEntity<Any> {
        logger.info("\nStarting to delete documents\n")
        try {
            val docList = documentMetadataService.findByContactId(contactId)
            docList.forEach { doc ->
                documentMetadataService.delete(doc.id)
                logger.info(
                    "\nDeleted success: \n\tname: ${doc.name}\n\t" +
                            "size: ${doc.size}\n\ttype: ${doc.type}\n\tdate: ${doc.date}"
                )
            }

            return ResponseEntity(mapOf("response" to "documents with $contactId deleted with success"), HttpStatus.OK)
        } catch (e: Exception) {
            logger.info("\nDeleted failed\n${e.message}\n")
            return ResponseEntity(mapOf("error" to e.message), HttpStatus.BAD_REQUEST)
        }
    }
}