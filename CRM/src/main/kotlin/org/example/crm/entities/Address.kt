package org.example.crm.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "address")
class Address(
    @Column(nullable = false)
    var address: String
) : BaseEntity() {
    @ManyToOne
    var contact: Contact? = null

    constructor(address: String, contact: Contact) : this(address) {
        this.contact = contact
    }
}
