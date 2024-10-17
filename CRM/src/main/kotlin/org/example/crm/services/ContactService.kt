package org.example.crm.services

import org.example.crm.dtos.ContactDTO
import org.example.crm.dtos.DocumentMetadataDTO
import org.example.crm.dtos.request.create.CContactDTO
import org.example.crm.dtos.request.filters.ContactParams
import org.example.crm.dtos.request.filters.CustomerParams
import org.example.crm.dtos.request.filters.PaginationParams
import org.example.crm.dtos.request.filters.ProfessionalParams
import org.example.crm.dtos.request.update.UAddressDTO
import org.example.crm.dtos.request.update.UContactDTO
import org.example.crm.dtos.request.update.UEmailDTO
import org.example.crm.dtos.request.update.UTelephoneDTO
import org.example.crm.entities.Contact
import org.example.crm.entities.Customer
import org.example.crm.entities.Professional
import org.springframework.web.multipart.MultipartFile

interface ContactService {
    fun addContact(newContact: CContactDTO): ContactDTO
    fun getContacts(
        paginationParams: PaginationParams,
        params: ContactParams
    ): List<ContactDTO>
    fun getContactsRows(
        params: ContactParams
    ): Int
    fun getCustomerById(customerId: Long): Customer

    fun getProfessionalById(professionalId: Long): Professional

    fun getCustomers(
        paginationParams: PaginationParams,
        params: CustomerParams
    ): List<ContactDTO>

    fun getProfessionals(
        paginationParams: PaginationParams,
        params: ProfessionalParams
    ): List<ContactDTO>

    fun getContactById(contactId: Long): Contact
    fun getDocumentsByContactId(jwtToken : String, xsrfToken : String, contactId: Long): Any?
    fun updateContact(contactId: Long, updatedContact: UContactDTO): ContactDTO
    fun deleteContact(contactId: Long)
    fun deleteDocumentsByContactId(jwtToken : String, xsrfToken : String, contactId: Long)
    fun addEmail(contactId: Long, newEmail: String): ContactDTO
    fun updateEmail(contactId: Long, emailId: Long, newEmail: UEmailDTO): ContactDTO
    fun deleteEmail(contactId: Long, emailId: Long): ContactDTO
    fun addTelephone(contactId: Long, newTelephone: String): ContactDTO
    fun updateTelephone(contactId: Long, telephoneId: Long, newTelephone: UTelephoneDTO): ContactDTO
    fun deleteTelephone(contactId: Long, telephoneId: Long): ContactDTO
    fun addAddress(contactId: Long, newAddress: String): ContactDTO
    fun updateAddress(contactId: Long, addressId: Long, newAddress: UAddressDTO): ContactDTO
    fun deleteAddress(contactId: Long, addressId: Long): ContactDTO
    fun newPhoneNumber(phoneNumber: String): Boolean
    fun newEmail(emailA: String): Boolean
}