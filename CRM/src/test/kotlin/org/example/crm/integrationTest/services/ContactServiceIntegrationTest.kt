package org.example.crm.integrationTest.services

import jakarta.persistence.EntityNotFoundException
import org.example.crm.dtos.ContactDTO
import org.example.crm.dtos.request.create.*
import org.example.crm.dtos.request.filters.ContactParams
import org.example.crm.dtos.request.filters.PaginationParams
import org.example.crm.dtos.request.update.*
import org.example.crm.integrationTest.controllers.IntegrationTest
import org.example.crm.services.ContactService
import org.example.crm.utils.enums.Category
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ContextConfiguration(initializers = [IntegrationTest.Initializer::class])
class ContactServiceIntegrationTest : IntegrationTest() {
    @Autowired
    private lateinit var contactService: ContactService

    private lateinit var contact: ContactDTO

    @BeforeEach
    fun setUp() {
        contact = contactService.addContact(
            CContactDTO(
                name = "Mario",
                surname = "Rossi",
                ssnCode = "RSSMRA99A99A999A",
                category = Category.CUSTOMER,
                emails = listOf(CEmailDTO(email = "mario.rossi@email.com")),
                addresses = listOf(CAddressDTO("test address")),
                telephones = listOf(CTelephoneDTO("1234567890")),
                customer = CCustomerDTO(),
            )
        )
    }

    @Test
    fun testCreateNewContact() {
        val createdContact = contactService.addContact(
            CContactDTO(
                name = "Mario",
                surname = "Rossi",
                ssnCode = "RSSMRA99A99A999A",
                category = Category.CUSTOMER,
                emails = listOf(CEmailDTO(email = "mario.rossi@email.com")),
                addresses = listOf(),
                telephones = listOf(),
                customer = CCustomerDTO(),
                professional = null
            )
        )

        Assertions.assertNotNull(createdContact)
        Assertions.assertEquals(contact.name, createdContact.name)
        Assertions.assertEquals(contact.surname, createdContact.surname)
        Assertions.assertEquals(contact.ssnCode, createdContact.ssnCode)
        Assertions.assertEquals(contact.category, createdContact.category)

    }

    @Test
    fun testGetContacts() {
        val pagination = PaginationParams()
        val params =
            ContactParams(name = "Mario", surname = "Rossi", category = Category.CUSTOMER)
        val contacts = contactService.getContacts(pagination, params)

        Assertions.assertNotNull(contacts)
        Assertions.assertTrue(contacts.isNotEmpty())
    }


    @Test
    fun testGetContactById() {
        val contact = contactService.getContactById(1)

        Assertions.assertNotNull(contact)
        Assertions.assertEquals(1, contact.id)
    }

    @Test
    fun updateContact() {
        val uContactDTO = UContactDTO(
            name = "Mario",
            surname = "Rossi",
            ssnCode = "RSSMRA99A99A999A",
            category = Category.CUSTOMER,
            emails = listOf(),
            addresses = listOf(),
            telephones = listOf(),
            customer = UCustomerDTO(),
            professional = null
        )
        val updatedContact = contactService.updateContact(contact.id, uContactDTO)

        Assertions.assertNotNull(updatedContact)
        Assertions.assertEquals(uContactDTO.name, updatedContact.name)
        Assertions.assertEquals(uContactDTO.surname, updatedContact.surname)
        Assertions.assertEquals(uContactDTO.ssnCode, updatedContact.ssnCode)
        Assertions.assertEquals(uContactDTO.category, updatedContact.category)
    }

    @Test
    fun testUpdateContact() {
        val uContactDTO = UContactDTO(
            name = "Mario",
            surname = "Rossi",
            ssnCode = "RSSMRA99A99A999A",
            category = Category.CUSTOMER,
            emails = listOf(),
            addresses = listOf(),
            telephones = listOf(),
            customer = UCustomerDTO(),
            professional = null
        )

        val updatedContact = contactService.updateContact(contact.id, uContactDTO)

        Assertions.assertNotNull(updatedContact)
        Assertions.assertEquals(uContactDTO.name, updatedContact.name)
        Assertions.assertEquals(uContactDTO.surname, updatedContact.surname)
        Assertions.assertEquals(uContactDTO.ssnCode, updatedContact.ssnCode)
        Assertions.assertEquals(uContactDTO.category, updatedContact.category)
    }

    @Test
    fun testDeleteContact() {
        contactService.deleteContact(contact.id)

        assertThrows<EntityNotFoundException> {
            contactService.getContactById(contact.id)
        }
    }

    @Test
    fun addEmail() {
        val newEmail = "maio.rossi@email.com"
        val updatedContact = contactService.addEmail(contact.id, newEmail)

        Assertions.assertNotNull(updatedContact)
        Assertions.assertEquals(newEmail, updatedContact.emails.find { it.email == newEmail }?.email)
    }


    @Test
    fun updateEmail() {
        val updatedEmail = "rossi.mario@email.com"
        val updatedContact = contactService.updateEmail(contact.id, contact.emails[0].id, UEmailDTO(updatedEmail))

        Assertions.assertNotNull(updatedContact)
        Assertions.assertEquals(updatedEmail, updatedContact.emails[0].email)
    }

    @Test
    fun updateEmailComment() {
        val newComment = "Test comment"
        val updatedContact =
            contactService.updateEmail(contact.id, contact.emails[0].id, UEmailDTO(comment = newComment))

        Assertions.assertNotNull(updatedContact)
        Assertions.assertEquals(newComment, updatedContact.emails[0].comment)
    }

    @Test
    fun deleteEmail() {
        val updatedContact = contactService.deleteEmail(contact.id, contact.emails[0].id)

        Assertions.assertNotNull(updatedContact)
        Assertions.assertTrue(updatedContact.emails.isEmpty())
    }

    @Test
    fun addTelephone() {
        val newTelephone = "1234567890"
        val updatedContact = contactService.addTelephone(contact.id, newTelephone)

        Assertions.assertNotNull(updatedContact)
        Assertions.assertEquals(newTelephone, updatedContact.telephones[0].telephone)
    }

    @Test
    fun updateTelephone() {
        val updatedTelephone = "0987654321"
        val updatedContact =
            contactService.updateTelephone(contact.id, contact.telephones[0].id, UTelephoneDTO(updatedTelephone))

        Assertions.assertNotNull(updatedContact)
        Assertions.assertEquals(
            updatedTelephone,
            updatedContact.telephones.find { it.telephone == updatedTelephone }?.telephone
        )
    }

    @Test
    fun deleteTelephone() {
        var updatedContact = contactService.addTelephone(contact.id, "1234567890")
        val totalTelephones = updatedContact.telephones.size
        updatedContact = contactService.deleteTelephone(contact.id, contact.telephones[0].id)

        Assertions.assertNotNull(updatedContact)
        Assertions.assertEquals(totalTelephones - 1, updatedContact.telephones.size)
    }

    @Test
    fun updateTelephoneComment() {
        val newComment = "Test comment"
        val updatedContact =
            contactService.updateTelephone(contact.id, contact.telephones[0].id, UTelephoneDTO(comment = newComment))

        Assertions.assertNotNull(updatedContact)
        Assertions.assertEquals(newComment, updatedContact.telephones[0].comment)
    }

    @Test
    fun addAddress() {
        val newAddress = "Via Roma 1"
        val updatedContact = contactService.addAddress(contact.id, newAddress)

        Assertions.assertNotNull(updatedContact)
        Assertions.assertEquals(newAddress, updatedContact.addresses[1].address)
    }

    @Test
    fun updateAddress() {
        val updatedAddress = "Via Milano 2"
        val updatedContact =
            contactService.updateAddress(contact.id, contact.addresses[0].id, UAddressDTO(updatedAddress))

        Assertions.assertNotNull(updatedContact)
        Assertions.assertEquals(updatedAddress, updatedContact.addresses[0].address)
    }

    @Test
    fun deleteAddress() {
        val updatedContact = contactService.deleteAddress(contact.id, contact.addresses[0].id)

        Assertions.assertNotNull(updatedContact)
        Assertions.assertTrue(updatedContact.addresses.isEmpty())
    }

    @Test
    fun updateAddressComment() {
        val newComment = "Test comment"
        val updatedContact =
            contactService.updateAddress(contact.id, contact.addresses[0].id, UAddressDTO(comment = newComment))

        Assertions.assertNotNull(updatedContact)
        Assertions.assertEquals(newComment, updatedContact.addresses[0].comment)
    }
}