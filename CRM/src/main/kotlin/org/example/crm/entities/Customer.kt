package org.example.crm.entities

import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import org.example.crm.dtos.request.create.CCustomerDTO

@Entity
class Customer() : BaseEntity() {
    @OneToOne(mappedBy = "customer")
    lateinit var contact: Contact

    @OneToMany(mappedBy = "customer")
    var jobOffers = mutableListOf<JobOffer>()

    constructor(contact: Contact, customerDTO: CCustomerDTO) : this() {
        this.contact = contact
        this.comment = customerDTO.comment
    }

}