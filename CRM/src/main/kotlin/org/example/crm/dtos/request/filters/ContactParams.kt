package org.example.crm.dtos.request.filters

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.example.crm.entities.Contact
import org.example.crm.repositories.specifications.ContactSpecs
import org.example.crm.utils.SSNCODE_REGEX
import org.example.crm.utils.enums.Category
import org.example.crm.validators.NullAndNotEmpty
import org.springframework.data.jpa.domain.Specification

open class ContactParams(
    //@field:NullAndNotEmpty(message = "Name cannot be empty")
    val name: String? = null,
    //@field:NullAndNotEmpty(message = "Surname cannot be empty")
    val surname: String? = null,
    //@field:NullAndNotEmpty(message = "SSN cannot be empty")
    //@field:Size(min = 1, max = 16, message = "SSN code length must be 16 characters")
    //@field:Pattern(regexp = SSNCODE_REGEX, message = "Invalid SSN code")
    val ssnCode: String? = null,
    val category: Category? = null,
    @field:NullAndNotEmpty(message = "Email cannot be empty")
    val email: String? = null,
    @field:NullAndNotEmpty(message = "Telephone cannot be empty")
    val telephone: String? = null
) {
    open fun toSpecification(category: Category? = null): Specification<Contact> {
        val cat = category ?: this.category
        return Specification.where<Contact>(null)
            .and(name?.let { ContactSpecs.nameStartWithIgnoreCase(it) })
            .and(surname?.let { ContactSpecs.surnameStartWithIgnoreCase(it) })
            .and(ssnCode?.let { ContactSpecs.ssnCodeStartWithIgnoreCase(it) })
            .and(cat?.let { ContactSpecs.categoryEqual(it) })
            .and(email?.let { ContactSpecs.emailEqual(it) })
            .and(telephone?.let { ContactSpecs.telephoneEqual(it) })
    }
}
