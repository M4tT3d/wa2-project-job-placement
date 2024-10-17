package org.example.crm.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import org.example.crm.dtos.request.create.CEmailDTO

@Entity
class Email(
    @Column(nullable = false) var email: String
) : BaseEntity() {

    @ManyToOne
    var contact: Contact? = null

    constructor(contact: Contact, emailDTO: CEmailDTO) : this(emailDTO.email) {
        this.contact = contact
        this.comment = emailDTO.comment
    }
}