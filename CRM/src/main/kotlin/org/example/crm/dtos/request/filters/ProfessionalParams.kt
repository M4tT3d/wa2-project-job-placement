package org.example.crm.dtos.request.filters

import org.example.crm.entities.Contact
import org.example.crm.repositories.specifications.ContactSpecs
import org.example.crm.utils.enums.Category
import org.example.crm.validators.NullAndNotEmpty
import org.springframework.data.jpa.domain.Specification

class ProfessionalParams(
    name: String? = null,
    surname: String? = null,
    ssnCode: String? = null,
    email: String? = null,
    telephone: String? = null,
    @field:NullAndNotEmpty(message = "Location cannot be empty")
    val location: String? = null,
    val dailyRate: Float? = null
    //TODO: add skill field
) : ContactParams(
    name = name,
    surname = surname,
    ssnCode = ssnCode,
    category = Category.PROFESSIONAL,
    email = email,
    telephone = telephone
) {
    fun toSpecification(): Specification<Contact> {
        return super.toSpecification(Category.PROFESSIONAL).and(
            location?.let { ContactSpecs.locationStartWithIgnoreCase(it) }
        ).and(
            dailyRate?.let { ContactSpecs.dailyRateGraterThanEqual(it) }
        )
    }
}
