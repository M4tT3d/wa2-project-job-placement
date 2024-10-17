package org.example.analytics.services

import org.example.analytics.dtos.CustomerDTO
import org.example.analytics.dtos.JobOfferStateDTO
import org.example.analytics.dtos.request.create.CCustomerDTO
import org.example.analytics.dtos.request.create.CJobOfferStateDTO
import org.example.analytics.dtos.request.delete.DCustomerDTO
import org.example.analytics.dtos.request.update.UJobOfferStateDTO

interface CustomerService {

    fun listAll(): List<CustomerDTO>

    fun addCustomer(newCustomer: CCustomerDTO): CustomerDTO

    fun addJobOffer(newJobOfferState: CJobOfferStateDTO): CustomerDTO

    fun updateJobOffer(updateJobOfferState: UJobOfferStateDTO): CustomerDTO

    fun getJobOffer(): JobOfferStateDTO

    fun delete(customer: DCustomerDTO)

    fun count(): Long

    fun max(): Long
}