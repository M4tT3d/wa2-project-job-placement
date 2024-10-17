package org.example.crm.entities

import jakarta.persistence.*
import org.example.crm.dtos.request.create.CContactDTO
import org.example.crm.utils.enums.Category

@Entity

class Contact(
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var surname: String,
) : BaseEntity() {
    @Column(columnDefinition = "varchar(16)")
    var ssnCode: String? = null

    @Enumerated(EnumType.STRING)
    var category: Category = Category.UNKNOWN

    @OneToMany(mappedBy = "contact", cascade = [CascadeType.ALL])
    var emails = mutableListOf<Email>()

    @OneToMany(mappedBy = "contact", cascade = [CascadeType.ALL])
    var addresses = mutableListOf<Address>()

    @OneToMany(mappedBy = "contact", cascade = [CascadeType.ALL])
    var telephones = mutableListOf<Telephone>()

    @OneToOne(cascade = [CascadeType.ALL])
    var customer: Customer? = null

    @OneToOne(cascade = [CascadeType.ALL])
    var professional: Professional? = null

    constructor(name: String, surname: String, comment: String?) : this(name, surname) {
        this.comment = comment
    }

    constructor(newContact: CContactDTO) : this(newContact.name, newContact.surname, newContact.comment) {
        this.ssnCode = newContact.ssnCode
        this.category = newContact.category
    }
}