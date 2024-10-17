package org.example.crm.dtos

import org.example.crm.entities.Contact
import org.example.crm.utils.enums.Category

data class ContactDTO(
    val id: Long,
    val name: String,
    val surname: String,
    val ssnCode: String? = null,
    val category: Category,
    val comment: String? = null,
    val professional: ProfessionalDTO? = null,
    val customer: CustomerDTO? = null,
    val emails: List<EmailDTO>,
    val addresses: List<AddressDTO>,
    val telephones: List<TelephoneDTO>
) {
    private fun toProfessionalResponseEntity() = mapOf(
        "id" to id,
        "name" to name,
        "surname" to surname,
        "ssnCode" to ssnCode,
        "category" to category,
        "comment" to comment,
        "emails" to emails.map {
            mapOf(
                "id" to it.id,
                "email" to it.email,
                "comment" to it.comment
            )
        },
        "addresses" to addresses.map {
            mapOf(
                "id" to it.id,
                "address" to it.address,
                "comment" to it.comment
            )
        },
        "telephones" to telephones.map {
            mapOf(
                "id" to it.id,
                "telephone" to it.telephone,
                "comment" to it.comment
            )
        },
        "professional" to professional?.toResponseEntity()
    )

    private fun toCustomerResponseEntity() = mapOf(
        "id" to id,
        "name" to name,
        "surname" to surname,
        "ssnCode" to ssnCode,
        "category" to category,
        "comment" to comment,
        "emails" to emails.map {
            mapOf(
                "id" to it.id,
                "email" to it.email,
                "comment" to it.comment
            )
        },
        "addresses" to addresses.map {
            mapOf(
                "id" to it.id,
                "address" to it.address,
                "comment" to it.comment
            )
        },
        "telephones" to telephones.map {
            mapOf(
                "id" to it.id,
                "telephone" to it.telephone,
                "comment" to it.comment
            )
        },
        "customer" to customer?.toResponseEntity()
    )

    fun toResponse() = when (category) {
        Category.PROFESSIONAL -> toProfessionalResponseEntity()
        Category.CUSTOMER -> toCustomerResponseEntity()
        Category.UNKNOWN -> mapOf(
            "id" to id,
            "name" to name,
            "surname" to surname,
            "ssnCode" to ssnCode,
            "category" to category,
            "comment" to comment,
            "emails" to emails.map {
                mapOf(
                    "id" to it.id,
                    "email" to it.email,
                    "comment" to it.comment
                )
            },
            "addresses" to addresses.map {
                mapOf(
                    "id" to it.id,
                    "address" to it.address,
                    "comment" to it.comment
                )
            },
            "telephones" to telephones.map {
                mapOf(
                    "id" to it.id,
                    "telephone" to it.telephone,
                    "comment" to it.comment
                )
            }
        )
    }
}

fun Contact.toProfessionalDTO() = ContactDTO(
    id = id,
    name = name,
    surname = surname,
    ssnCode = ssnCode,
    category = category,
    comment = comment,
    emails = emails.map { it.toDTO() },
    addresses = addresses.map { it.toDTO() },
    telephones = telephones.map { it.toDTO() },
    professional = professional?.toDTO()
)

fun Contact.toCustomerDTO() = ContactDTO(
    id = id,
    name = name,
    surname = surname,
    ssnCode = ssnCode,
    category = category,
    comment = comment,
    emails = emails.map { it.toDTO() },
    addresses = addresses.map { it.toDTO() },
    telephones = telephones.map { it.toDTO() },
    customer = customer?.toDTO()
)

fun Contact.toDTO() = ContactDTO(
    id = id,
    name = name,
    surname = surname,
    ssnCode = ssnCode,
    category = category,
    comment = comment,
    professional = professional?.toDTO(),
    customer = customer?.toDTO(),
    emails = emails.map { it.toDTO() },
    addresses = addresses.map { it.toDTO() },
    telephones = telephones.map { it.toDTO() }
)