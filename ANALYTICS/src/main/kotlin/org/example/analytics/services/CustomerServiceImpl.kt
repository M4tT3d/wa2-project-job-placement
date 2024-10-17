package org.example.analytics.services

import org.example.analytics.documents.Customer
import org.example.analytics.dtos.CustomerDTO
import org.example.analytics.dtos.JobOfferStateDTO
import org.example.analytics.dtos.request.create.CCustomerDTO
import org.example.analytics.dtos.request.create.CJobOfferStateDTO
import org.example.analytics.dtos.request.delete.DCustomerDTO
import org.example.analytics.dtos.request.update.UJobOfferStateDTO
import org.example.analytics.dtos.toDTO
import org.example.analytics.repositories.CustomerRepository
import org.example.analytics.utils.StateJobOffer
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CustomerServiceImpl(
    private val customerRepository: CustomerRepository
) : CustomerService {

    override fun listAll(): List<CustomerDTO> {
        return customerRepository.findAll().map { it.toDTO() }
    }

    override fun addCustomer(newCustomer: CCustomerDTO): CustomerDTO {
        val customer = Customer(
            customerId = newCustomer.customerId,
            created = 0,
            aborted = 0,
            selection_phase = 0,
            candidate_proposal = 0,
            consolidated = 0,
            done = 0
        )
        return customerRepository.save(customer).toDTO()
    }

    override fun addJobOffer(newJobOfferState: CJobOfferStateDTO): CustomerDTO {
        val customer = customerRepository.findByCustomerId(newJobOfferState.customerId)
            ?: throw IllegalArgumentException("Customer not found")
        customer.created += 1
        return customerRepository.save(customer).toDTO()
    }

    override fun updateJobOffer(updateJobOfferState: UJobOfferStateDTO): CustomerDTO {
        val customer = customerRepository.findByCustomerId(updateJobOfferState.customerId)
            ?: throw IllegalArgumentException("Customer not found")
        when {
            updateJobOfferState.jobOfferState_old == StateJobOffer.CREATED && updateJobOfferState.jobOfferState_new == StateJobOffer.ABORTED -> {
                customer.created -= 1
                customer.aborted += 1
            }

            updateJobOfferState.jobOfferState_old == StateJobOffer.CREATED && updateJobOfferState.jobOfferState_new == StateJobOffer.SELECTION_PHASE -> {
                customer.created -= 1
                customer.selection_phase += 1
            }

            updateJobOfferState.jobOfferState_old == StateJobOffer.SELECTION_PHASE && updateJobOfferState.jobOfferState_new == StateJobOffer.ABORTED -> {
                customer.selection_phase -= 1
                customer.aborted += 1
            }

            updateJobOfferState.jobOfferState_old == StateJobOffer.SELECTION_PHASE && updateJobOfferState.jobOfferState_new == StateJobOffer.CANDIDATE_PROPOSAL -> {
                customer.selection_phase -= 1
                customer.candidate_proposal += 1
            }

            updateJobOfferState.jobOfferState_old == StateJobOffer.CANDIDATE_PROPOSAL && updateJobOfferState.jobOfferState_new == StateJobOffer.ABORTED -> {
                customer.candidate_proposal -= 1
                customer.aborted += 1
            }

            updateJobOfferState.jobOfferState_old == StateJobOffer.CANDIDATE_PROPOSAL && updateJobOfferState.jobOfferState_new == StateJobOffer.CONSOLIDATED -> {
                customer.candidate_proposal -= 1
                customer.consolidated += 1
            }

            updateJobOfferState.jobOfferState_old == StateJobOffer.CANDIDATE_PROPOSAL && updateJobOfferState.jobOfferState_new == StateJobOffer.SELECTION_PHASE -> {
                customer.candidate_proposal -= 1
                customer.selection_phase += 1
            }

            updateJobOfferState.jobOfferState_old == StateJobOffer.CONSOLIDATED && updateJobOfferState.jobOfferState_new == StateJobOffer.ABORTED -> {
                customer.consolidated -= 1
                customer.aborted += 1
            }

            updateJobOfferState.jobOfferState_old == StateJobOffer.CONSOLIDATED && updateJobOfferState.jobOfferState_new == StateJobOffer.DONE -> {
                customer.consolidated -= 1
                customer.done += 1
            }

            updateJobOfferState.jobOfferState_old == StateJobOffer.CONSOLIDATED && updateJobOfferState.jobOfferState_new == StateJobOffer.SELECTION_PHASE -> {
                customer.consolidated -= 1
                customer.selection_phase += 1
            }
        }
        return customerRepository.save(customer).toDTO()
    }

    override fun getJobOffer(): JobOfferStateDTO {
        val customers = customerRepository.findAll()
        return JobOfferStateDTO(
            created = customers.sumOf { it.created },
            aborted = customers.sumOf { it.aborted },
            selection_phase = customers.sumOf { it.selection_phase },
            candidate_proposal = customers.sumOf { it.candidate_proposal },
            consolidated = customers.sumOf { it.consolidated },
            done = customers.sumOf { it.done },
        )
    }

    override fun delete(customer: DCustomerDTO) {
        val customerDB = customerRepository.findByCustomerId(customer.id)
        if (customerDB != null) {
            customerRepository.delete(customerDB)
        }
    }

    override fun count(): Long {
        return customerRepository.count()
    }

    override fun max(): Long {
        val customers = customerRepository.findAll()
        val customerReduce = customers.map { customer ->
            mapOf(
                "customerId" to customer.customerId,
                "total" to customer.created + customer.aborted + customer.selection_phase + customer.candidate_proposal + customer.consolidated + customer.done
            )
        }.maxByOrNull { it["total"] as Long }
        return customerReduce?.get("customerId") as Long
    }
}