package org.example.crm.dtos.request.filters

import org.example.crm.entities.Contact
import org.example.crm.utils.enums.Category
import org.springframework.data.jpa.domain.Specification

class CustomerParams(
    name: String? = null,
    surname: String? = null,
    ssnCode: String? = null,
    email: String? = null,
    telephone: String? = null,
) : ContactParams(
    name = name,
    surname = surname,
    ssnCode = ssnCode,
    category = Category.CUSTOMER,
    email = email,
    telephone = telephone
) {
    fun toSpecification(): Specification<Contact> {
        return super.toSpecification(Category.CUSTOMER)
    }
}
