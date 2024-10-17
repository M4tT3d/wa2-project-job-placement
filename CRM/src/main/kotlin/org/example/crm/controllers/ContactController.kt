package org.example.crm.controllers

import jakarta.servlet.http.HttpServletRequest
import org.example.crm.dtos.request.create.CAddressDTO
import org.example.crm.dtos.request.create.CContactDTO
import org.example.crm.dtos.request.create.CEmailDTO
import org.example.crm.dtos.request.create.CTelephoneDTO
import org.example.crm.dtos.request.filters.ContactParams
import org.example.crm.dtos.request.filters.PaginationParams
import org.example.crm.dtos.request.update.UAddressDTO
import org.example.crm.dtos.request.update.UContactDTO
import org.example.crm.dtos.request.update.UEmailDTO
import org.example.crm.dtos.request.update.UTelephoneDTO
import org.example.crm.dtos.toDTO
import org.example.crm.services.ContactService
import org.example.crm.services.JobOfferService
import org.example.crm.utils.enums.Category
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/contacts")
class ContactController(
    private val contactService: ContactService
) {
    private val logger = LoggerFactory.getLogger(ContactController::class.java)
    val restTemplate = RestTemplate()

    @PostMapping(
        "",
        "/",
        //consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun addContact(
        request: HttpServletRequest,
        @Validated @RequestBody contact: CContactDTO,
    ): Map<String, Any> {

        logger.info("Adding contact")
        val newContact = contactService.addContact(contact)
        logger.info("Contact added")
        return mapOf("contact" to newContact.toResponse())
    }

    @GetMapping(
        "",
        "/",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getAll(
        @Validated paginationParams: PaginationParams,
        @Validated params: ContactParams
    ): ResponseEntity<Map<String, Any?>> {
        logger.info("Getting all contacts")
        val contacts = contactService.getContacts(paginationParams, params).map { it.toResponse() }
        val rowCount = contactService.getContactsRows(params)
        logger.info("Contacts retrieved")
        return ResponseEntity(mapOf("contacts" to contacts, "rowCount" to rowCount), HttpStatus.OK)
    }

    @GetMapping(
        "/{contactId}",
        "/{contactId}/",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getContact(
        @CookieValue(name = "XSRF-TOKEN", required = false) xsrfToken: String?,
        @PathVariable contactId: Long
    ): ResponseEntity<Map<String, Any?>> {
        logger.info("Getting contact info")
        val contact = contactService.getContactById(contactId)
        logger.info("Contact retrieved")
        val jwt = SecurityContextHolder.getContext().authentication.credentials as Jwt
        val jwtToken = jwt.tokenValue
        val documents = contactService.getDocumentsByContactId(jwtToken, xsrfToken?: "", contactId)
        val response = ResponseEntity(mapOf("contact" to contact.toDTO().toResponse(), "documents" to documents), HttpStatus.OK)
        logger.info("Response: ${response.body}")
        return if (documents!=null) {
            response
        } else {
            ResponseEntity(mapOf("contact" to contact.toDTO().toResponse()), HttpStatus.OK)
        }
    }

    @PatchMapping(
        "/{contactId}",
        "/{contactId}/",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updateContact(
        @PathVariable contactId: Long,
        @Validated @RequestBody contact: UContactDTO
    ): ResponseEntity<Map<String, Any?>> {
        logger.info("Updating contact info")
        val updatedContact = contactService.updateContact(contactId, contact)
        logger.info("Contact updated")
        return ResponseEntity(mapOf("contact" to updatedContact.toResponse()), HttpStatus.OK)
    }

    @DeleteMapping(
        "/{contactId}",
        "/{contactId}/",
    )
    fun deleteContact(
        @CookieValue(name = "XSRF-TOKEN", required = false) xsrfToken: String?,
        @PathVariable contactId: Long
    ): ResponseEntity<Any> {
        logger.info("Deleting contact")
        contactService.deleteContact(contactId)
        logger.info("Contact deleted")
        val jwt = SecurityContextHolder.getContext().authentication.credentials as Jwt
        val jwtToken = jwt.tokenValue
        contactService.deleteDocumentsByContactId(jwtToken, xsrfToken?: "", contactId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping(
        "/{contactId}/email",
        "/{contactId}/email/",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun addEmailToContact(
        @PathVariable contactId: Long,
        @Validated @RequestBody email: CEmailDTO
    ): ResponseEntity<Map<String, Any>> {
        logger.info("Adding email to contact $contactId")
        val newContact = contactService.addEmail(contactId, email.email)
        logger.info("Email added to $contactId")
        return ResponseEntity(mapOf("contact" to newContact.toResponse()), HttpStatus.CREATED)
    }

    @PatchMapping(
        "/{contactId}/email/{emailId}",
        "/{contactId}/email/{emailId}/",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updateEmailFromContact(
        @PathVariable contactId: Long,
        @PathVariable emailId: Long,
        @Validated @RequestBody email: UEmailDTO
    ): ResponseEntity<Map<String, Any>> {
        logger.info("Updating email data from contact $contactId")
        val newContact = contactService.updateEmail(contactId, emailId, email)
        logger.info("Email data updated from $contactId")
        return ResponseEntity(mapOf("contact" to newContact.toResponse()), HttpStatus.OK)
    }

    @DeleteMapping(
        "/{contactId}/email/{emailId}",
        "/{contactId}/email/{emailId}/",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun deleteEmailFromContact(
        @PathVariable contactId: Long,
        @PathVariable emailId: Long,
    ): ResponseEntity<Any?> {
        logger.info("Deleting email from contact $contactId")
        contactService.deleteEmail(contactId, emailId)
        logger.info("Email deleted from $contactId")
        return ResponseEntity.noContent().build()
    }

    @PostMapping(
        "/{contactId}/telephone",
        "/{contactId}/telephone/",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun addTelephoneToContact(
        @PathVariable contactId: Long,
        @Validated @RequestBody telephone: CTelephoneDTO
    ): ResponseEntity<Map<String, Any>> {
        logger.info("Adding telephone to contact $contactId")
        val newContact = contactService.addTelephone(contactId, telephone.telephone)
        logger.info("Telephone added to $contactId")
        return ResponseEntity(mapOf("contact" to newContact.toResponse()), HttpStatus.CREATED)
    }

    @PatchMapping(
        "/{contactId}/telephone/{telephoneId}",
        "/{contactId}/telephone/{telephoneId}/",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updateTelephoneFromContact(
        @PathVariable contactId: Long,
        @PathVariable telephoneId: Long,
        @Validated @RequestBody telephone: UTelephoneDTO
    ): ResponseEntity<Map<String, Any>> {
        logger.info("Updating telephone data from contact $contactId")
        val contact = contactService.updateTelephone(contactId, telephoneId, telephone)
        logger.info("Telephone data updated from $contactId")
        return ResponseEntity(mapOf("contact" to contact.toResponse()), HttpStatus.OK)
    }

    @DeleteMapping(
        "/{contactId}/telephone/{telephoneId}",
        "/{contactId}/telephone/{telephoneId}/",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun deleteTelephoneFromContact(
        @PathVariable contactId: Long,
        @PathVariable telephoneId: Long,
    ): ResponseEntity<Any?> {
        logger.info("Deleting telephone from contact $contactId")
        contactService.deleteTelephone(contactId, telephoneId)
        logger.info("Telephone deleted from $contactId")
        return ResponseEntity.noContent().build()
    }

    @PostMapping(
        "/{contactId}/address",
        "/{contactId}/address/",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun addAddressToContact(
        @PathVariable contactId: Long,
        @Validated @RequestBody address: CAddressDTO
    ): ResponseEntity<Map<String, Any>> {
        logger.info("Adding address to contact $contactId")
        val newContact = contactService.addAddress(contactId, address.address)
        logger.info("Address added to $contactId")
        return ResponseEntity(mapOf("contact" to newContact.toResponse()), HttpStatus.CREATED)
    }

    @PatchMapping(
        "/{contactId}/address/{addressId}",
        "/{contactId}/address/{addressId}/",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updateAddressFromContact(
        @PathVariable contactId: Long,
        @PathVariable addressId: Long,
        @Validated @RequestBody address: UAddressDTO
    ): ResponseEntity<Map<String, Any>> {
        logger.info("Updating address from contact $contactId")
        val contact = contactService.updateAddress(contactId, addressId, address)
        logger.info("Address updated from $contactId")
        return ResponseEntity(mapOf("contact" to contact.toResponse()), HttpStatus.OK)
    }

    @DeleteMapping(
        "/{contactId}/address/{addressId}",
        "/{contactId}/address/{addressId}/",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun deleteAddressFromContact(
        @PathVariable contactId: Long,
        @PathVariable addressId: Long,
    ): ResponseEntity<Any?> {
        logger.info("Deleting address from contact $contactId")
        contactService.deleteAddress(contactId, addressId)
        logger.info("Address deleted from $contactId")
        return ResponseEntity.noContent().build()
    }
}