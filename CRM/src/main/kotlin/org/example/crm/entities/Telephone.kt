package org.example.crm.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne

@Entity
class Telephone(
    @Column(nullable = false)
    var telephone: String
) : BaseEntity() {
    @ManyToOne
    var contact: Contact? = null

    constructor(telephone: String, contact: Contact) : this(telephone) {
        this.contact = contact
    }
}