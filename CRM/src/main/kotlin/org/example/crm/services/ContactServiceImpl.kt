package org.example.crm.services

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.example.crm.dtos.ContactDTO
import org.example.crm.dtos.request.create.CContactDTO
import org.example.crm.dtos.request.create.CProfessionalDTO
import org.example.crm.dtos.request.filters.ContactParams
import org.example.crm.dtos.request.filters.CustomerParams
import org.example.crm.dtos.request.filters.PaginationParams
import org.example.crm.dtos.request.filters.ProfessionalParams
import org.example.crm.dtos.request.update.UAddressDTO
import org.example.crm.dtos.request.update.UContactDTO
import org.example.crm.dtos.request.update.UEmailDTO
import org.example.crm.dtos.request.update.UTelephoneDTO
import org.example.crm.dtos.toCustomerDTO
import org.example.crm.dtos.toDTO
import org.example.crm.dtos.toProfessionalDTO
import org.example.crm.entities.*
import org.example.crm.repositories.*
import org.example.crm.utils.enums.Category
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.core.ParameterizedTypeReference
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
@Transactional
@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
class ContactServiceImpl(
    private val contactRepo: ContactRepository,
    private val emailRepo: EmailRepository,
    private val telephoneRepo: TelephoneRepository,
    private val addressRepo: AddressRepository,
    private val professionalRepo: ProfessionalRepository,
    private val customerRepo: CustomerRepository,
    private val skillRepo: SkillRepository,
    private val jobOfferService: JobOfferService,
) : ContactService {

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, String>
    private val logger = LoggerFactory.getLogger(ContactService::class.java)

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun addContact(newContact: CContactDTO): ContactDTO {
        val contact = contactRepo.save(Contact(newContact))

        contact.emails = newContact.emails?.map { Email(it.email).apply { this.contact = contact } }?.toMutableList()
            ?: mutableListOf()
        contact.telephones =
            newContact.telephones?.map { Telephone(it.telephone).apply { this.contact = contact } }?.toMutableList()
                ?: mutableListOf()
        contact.addresses =
            newContact.addresses?.map { Address(it.address).apply { this.contact = contact } }?.toMutableList()
                ?: mutableListOf()
        emailRepo.saveAll(contact.emails)
        telephoneRepo.saveAll(contact.telephones)
        addressRepo.saveAll(contact.addresses)

        if (contact.category == Category.PROFESSIONAL && newContact.professional != null) {
            try {
                val message = "{ \"employmentState\":\"${newContact.professional.employmentState}\"}"
                kafkaTemplate.send("contact_professional", message)
                logger.info("kafka send message for new professional")
            } catch (e: Exception) {
                logger.error("Error while sending message to Kafka", e)
            }
            val skills: Set<Skill> =
                skillRepo.findAll().filter { newContact.professional.skills.contains(it.id) }.toSet()
            contact.professional = professionalRepo.save(
                Professional(
                    newContact.professional,
                    contact,
                    skills
                )
            )
            val pSkills: String =
                skillRepo.findAll()
                    .filter { newContact.professional.skills.contains(it.id) }
                    .joinToString("; ") { "id:${it.id},name:${it.skill}" }
            try {
                val message = "{ \"professionalSkills\":\"${pSkills}\"}"
                kafkaTemplate.send("p_skills", message)
                logger.info("kafka send message for new professional")
            } catch (e: Exception) {
                logger.error("Error while sending message to Kafka", e)
            }
            return contact.toProfessionalDTO()
        } else if (contact.category == Category.CUSTOMER && newContact.customer != null) {
            contact.customer = customerRepo.save(Customer(contact, newContact.customer))
            try {
                val message = " {\"customerId\":\"${contact.customer?.id}\"}"
                kafkaTemplate.send("contact_customer", message)
                logger.info("kafka send message for new customer")
            } catch (e: Exception) {
                logger.error("Error while sending message to Kafka", e)
            }
            return contact.toCustomerDTO()
        }


        return contact.toDTO()
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    fun getContactWithSpec(page: PageRequest, spec: Specification<Contact>): List<Contact> {
        return contactRepo.findAll(spec, page).content
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun getContacts(
        paginationParams: PaginationParams,
        params: ContactParams
    ): List<ContactDTO> {
        return getContactWithSpec(paginationParams.toPageRequest(), params.toSpecification()).map { it.toDTO() }
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun getContactsRows(params: ContactParams): Int {
        return contactRepo.findAll(params.toSpecification()).size
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun getContactById(contactId: Long): Contact {
        val contact = contactRepo.findById(contactId).orElse(null) ?: throw EntityNotFoundException("Contact not found")
        return contact
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun getDocumentsByContactId(jwtToken: String, xsrfToken: String, contactId: Long): Any? {
        val restTemplate = RestTemplate()
        val headers = HttpHeaders().apply {
            set("X-XSRF-TOKEN", xsrfToken)
            set("Authorization", "Bearer $jwtToken")
        }
        var resp: Any? = null
        try {
            resp = restTemplate.exchange(
                "http://localhost:8084/api/documents/contacts/$contactId",
                HttpMethod.GET,
                HttpEntity(null, headers),
                object : ParameterizedTypeReference<Any?>() {}
            ).body
        } catch (e: Exception) {
//            return emptyList<Any?>()
        }
        return resp
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    fun updateCategoryData(contact: Contact, updatedContact: UContactDTO) {
        when (contact.category) {
            Category.PROFESSIONAL -> {
                val oldSkills = contact.professional?.skills?.joinToString("-") { it.id.toString() }.orEmpty()
                val newSkills = skillRepo.findAll()
                    .filter { updatedContact.professional?.skills?.contains(it.id) == true }
                    .joinToString("; ") { "id:${it.id},name:${it.skill}" }
                try {
                    val message = "{ \"oldSkills\":\"${oldSkills}\",\"newSkills\":\"${newSkills}\"}"
                    kafkaTemplate.send("p_skills", message)
                    logger.info("kafka send message for update professional skills")
                } catch (e: Exception) {
                    logger.error("Error while sending message to Kafka", e)
                }
                contact.professional?.apply {
                    skills =
                        if (updatedContact.professional?.skills != null && updatedContact.professional.skills.isNotEmpty()) skillRepo.findAll()
                            .filter { updatedContact.professional.skills.contains(it.id) }
                            .toMutableSet() else skills
                    employmentState = updatedContact.professional?.employmentState ?: employmentState
                    location = updatedContact.professional?.location ?: location
                    dailyRate = updatedContact.professional?.dailyRate ?: dailyRate
                    comment = updatedContact.professional?.comment ?: comment
                }
            }

            Category.CUSTOMER -> {
                contact.customer?.apply {
                    comment = updatedContact.customer?.comment?.trim() ?: comment
                }
            }

            Category.UNKNOWN -> {
                if (updatedContact.category == Category.PROFESSIONAL) {
                    val oldSkills = contact.professional?.skills?.joinToString("-") { it.id.toString() }.orEmpty()
                    val newSkills = skillRepo.findAll()
                        .filter { updatedContact.professional?.skills?.contains(it.id) == true }
                        .joinToString("; ") { "id:${it.id},name:${it.skill}" }
                    try {
                        val message = "{ \"oldSkills\":\"${oldSkills}\"},{\"newSkills\":\"${newSkills}\"}"
                        kafkaTemplate.send("p_skills", message)
                        logger.info("kafka send message for update professional skills")
                    } catch (e: Exception) {
                        logger.error("Error while sending message to Kafka", e)
                    }
                    val professional = updatedContact.professional
                    if (professional?.skills.isNullOrEmpty() || professional?.dailyRate == null || professional.employmentState == null)
                        throw IllegalArgumentException("Professional data is missing")
                    val skills =
                        skillRepo.findAll().filter { professional.skills?.contains(it.id) == true }.toSet()
                    if (skills.isEmpty()) throw IllegalArgumentException("Skills not found")
                    contact.professional = Professional(
                        CProfessionalDTO(
                            employmentState = professional.employmentState,
                            location = professional.location,
                            dailyRate = professional.dailyRate,
                            comment = professional.comment
                        ), contact,
                        skills
                    )
                    contact.category = updatedContact.category
                } else if (updatedContact.category == Category.CUSTOMER) {
                    contact.customer = Customer()
                    contact.customer?.comment = updatedContact.customer?.comment?.trim() ?: ""
                    contact.category = updatedContact.category
                }
            }
        }

    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun updateContact(contactId: Long, updatedContact: UContactDTO): ContactDTO {
        val contact = getContactById(contactId)
        if (updatedContact.category != null) {
            if (updatedContact.category != contact.category && contact.category != Category.UNKNOWN)
                throw IllegalArgumentException("It is not possible to change the category of a contact")
            updateCategoryData(contact, updatedContact)
        }
        contact.name = updatedContact.name?.trim() ?: contact.name
        contact.surname = updatedContact.surname?.trim() ?: contact.surname
        contact.ssnCode = updatedContact.ssnCode?.uppercase() ?: contact.ssnCode
        contact.comment = updatedContact.comment?.trim() ?: contact.comment

        emailRepo.findByContactId(contactId).forEach { emailRepo.delete(it) }
        contact.emails.clear()
        updatedContact.emails?.forEach { contact.emails.add(Email(contact, it)) }

        telephoneRepo.findByContactId(contactId).forEach { telephoneRepo.delete(it) }
        contact.telephones.clear()
        updatedContact.telephones?.forEach { contact.telephones.add(Telephone(it.telephone, contact)) }

        addressRepo.findByContactId(contactId).forEach { addressRepo.delete(it) }
        contact.addresses.clear()
        updatedContact.addresses?.forEach { contact.addresses.add(Address(it.address, contact)) }
        emailRepo.saveAll(contact.emails)
        telephoneRepo.saveAll(contact.telephones)
        addressRepo.saveAll(contact.addresses)
        contactRepo.save(contact)
        return contact.toDTO()
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun deleteContact(contactId: Long) {
        val contact = getContactById(contactId)

        if (contact.category == Category.PROFESSIONAL) {
            jobOfferService.deleteByProfessional(contact.professional?.id)
            val pSkills: String = contact.professional?.skills?.joinToString(";") { it.id.toString() }.orEmpty()
            try {
                val message =
                    "{ \"deleteState\":\"${contact.professional?.employmentState}\", \"deleteSkills\":\"${pSkills}\"}"
                kafkaTemplate.send("delete_professional", message)
                logger.info("kafka send message for deletion of the professional")
            } catch (e: Exception) {
                logger.error("Error while sending message to Kafka", e)
            }

        } else {
            jobOfferService.deleteByCustomer(contact.customer?.id)
            try {
                val message = "{ \"id\":\"${contact.customer?.id}\"}"
                kafkaTemplate.send("delete_customer", message)
                logger.info("kafka send message for deletion of the customer")
            } catch (e: Exception) {
                logger.error("Error while sending message to Kafka", e)
            }
        }
        contactRepo.delete(contact)
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun deleteDocumentsByContactId(jwtToken: String, xsrfToken: String, contactId: Long) {
        val restTemplate = RestTemplate()
        val headers = HttpHeaders().apply {
            set("X-XSRF-TOKEN", xsrfToken)
            set("Authorization", "Bearer $jwtToken")
        }
        restTemplate.exchange(
            "http://localhost:8084/api/documents/contacts/$contactId",
            HttpMethod.DELETE,
            HttpEntity(null, headers),
            object : ParameterizedTypeReference<Unit>() {}
        )
    }


    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun addEmail(contactId: Long, newEmail: String): ContactDTO {
        val contact = getContactById(contactId)
        val email = Email(newEmail)
        email.contact = contact
        contact.emails.add(email)
        emailRepo.save(email)
        contactRepo.save(contact)
        return contact.toDTO()
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun updateEmail(contactId: Long, emailId: Long, newEmail: UEmailDTO): ContactDTO {
        val contact = getContactById(contactId)
        val email = emailRepo.findById(emailId).orElse(null) ?: throw EntityNotFoundException("Email not found")
        if (contact.emails.find { it.id == email.id } == null) throw IllegalArgumentException("Email not found in contact")
        email.email = newEmail.email ?: email.email
        email.comment = newEmail.comment ?: email.comment
        emailRepo.save(email)
        contactRepo.save(contact)
        return contact.toDTO()
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun deleteEmail(contactId: Long, emailId: Long): ContactDTO {
        val contact = getContactById(contactId)
        val email = emailRepo.findById(emailId).orElse(null) ?: throw EntityNotFoundException("Email not found")
        if (contact.emails.find { it.id == email.id } == null) throw IllegalArgumentException("Email not found in contact")
        contact.emails.remove(email)
        emailRepo.delete(email)
        return contact.toDTO()
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun addTelephone(contactId: Long, newTelephone: String): ContactDTO {
        val contact = getContactById(contactId)
        val telephone = Telephone(newTelephone)
        contact.telephones.add(telephone)
        telephone.contact = contact
        telephoneRepo.save(telephone)
        return contact.toDTO()
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun updateTelephone(contactId: Long, telephoneId: Long, newTelephone: UTelephoneDTO): ContactDTO {
        val contact = getContactById(contactId)
        val telephone =
            telephoneRepo.findById(telephoneId).orElse(null) ?: throw EntityNotFoundException("Telephone not found")
        if (contact.telephones.find { it.id == telephone.id } == null) throw IllegalArgumentException("Telephone not found in contact")
        telephone.telephone = newTelephone.telephone ?: telephone.telephone
        telephone.comment = newTelephone.comment ?: telephone.comment
        telephoneRepo.save(telephone)
        contactRepo.save(contact)
        return contact.toDTO()
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun deleteTelephone(contactId: Long, telephoneId: Long): ContactDTO {
        val contact = getContactById(contactId)
        val telephone =
            telephoneRepo.findById(telephoneId).orElse(null) ?: throw EntityNotFoundException("Telephone not found")
        if (contact.telephones.find { it.id == telephone.id } == null) throw IllegalArgumentException("Telephone not found in contact")
        contact.telephones.remove(telephone)
        telephoneRepo.delete(telephone)
        return contact.toDTO()
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun addAddress(contactId: Long, newAddress: String): ContactDTO {
        val contact = getContactById(contactId)
        val address = Address(newAddress)
        contact.addresses.add(address)
        address.contact = contact
        addressRepo.save(address)
        return contact.toDTO()
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun updateAddress(contactId: Long, addressId: Long, newAddress: UAddressDTO): ContactDTO {
        val contact = getContactById(contactId)
        val address = addressRepo.findById(addressId).orElse(null) ?: throw EntityNotFoundException("Address not found")
        if (contact.addresses.find { it.id == address.id } == null) throw IllegalArgumentException("Address not found in contact")
        address.address = newAddress.address ?: address.address
        address.comment = newAddress.comment ?: address.comment
        addressRepo.save(address)
        return contact.toDTO()
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun deleteAddress(contactId: Long, addressId: Long): ContactDTO {
        val contact = getContactById(contactId)
        val address = addressRepo.findById(addressId).orElse(null) ?: throw EntityNotFoundException("Address not found")
        if (contact.addresses.find { it.id == address.id } == null) throw IllegalArgumentException("Address not found in contact")
        contact.addresses.remove(address)
        addressRepo.delete(address)
        return contact.toDTO()
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun newPhoneNumber(phoneNumber: String): Boolean {
        val telephones = telephoneRepo.findByTelephone(phoneNumber)
        if (telephones.isNotEmpty()) return true
        val contact = Contact(name = "?", surname = "?")
        val telephone = Telephone(phoneNumber)
        telephone.contact = contact
        contact.telephones.add(telephone)
        contactRepo.save(contact)
        telephoneRepo.save(telephone)
        return false
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun newEmail(emailA: String): Boolean {
        val emails = emailRepo.findByEmail(emailA)
        if (emails.isNotEmpty()) return true
        val contact = Contact(name = "?", surname = "?")
        val email = Email(emailA)
        email.contact = contact
        contact.emails.add(email)
        contactRepo.save(contact)
        emailRepo.save(email)
        return false
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun getCustomers(
        paginationParams: PaginationParams,
        params: CustomerParams
    ): List<ContactDTO> {
        return getContactWithSpec(paginationParams.toPageRequest(), params.toSpecification()).map { it.toDTO() }
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun getProfessionals(
        paginationParams: PaginationParams,
        params: ProfessionalParams
    ): List<ContactDTO> {
        return getContactWithSpec(
            paginationParams.toPageRequest(),
            params.toSpecification()
        ).map { it.toProfessionalDTO() }
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun getCustomerById(customerId: Long): Customer {
        val customer =
            customerRepo.findById(customerId)
        if (!customer.isPresent) {
            throw EntityNotFoundException("This id (${customerId}) does not exist")
        }
        return customer.get()
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun getProfessionalById(professionalId: Long): Professional {
        val professional =
            professionalRepo.findById(professionalId)
        if (!professional.isPresent) {
            throw EntityNotFoundException("This id (${professionalId}) does not exist")
        }
        return professional.get()
    }
}