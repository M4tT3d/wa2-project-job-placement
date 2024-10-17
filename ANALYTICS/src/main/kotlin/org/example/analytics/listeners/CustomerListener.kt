package org.example.analytics.listeners

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.example.analytics.dtos.request.create.CCustomerDTO
import org.example.analytics.dtos.request.create.CJobOfferStateDTO
import org.example.analytics.dtos.request.delete.DCustomerDTO
import org.example.analytics.dtos.request.update.UJobOfferStateDTO
import org.example.analytics.services.CustomerService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class CustomerListener(
    val customerService: CustomerService
) {
    private val logger = LoggerFactory.getLogger(CustomerListener::class.java)
    private val mapper = jacksonObjectMapper()

    @KafkaListener(id = "contact_customer", topics = ["contact_customer"], groupId = "analytics")
    fun customer(customerString: String) {
        val customerMap: Map<String, Any> = mapper.readValue(customerString)
        logger.info("Received message on contact_customer topic")
        if (customerMap.size == 1) {
            val customer: CCustomerDTO = mapper.readValue(customerString, CCustomerDTO::class.java)
            customerService.addCustomer(customer)
            logger.info("Customer added")
        }
        if (customerMap.size == 2) {
            val newJobOffer: CJobOfferStateDTO = mapper.readValue(customerString, CJobOfferStateDTO::class.java)
            customerService.addJobOffer(newJobOffer)
            logger.info("Job offer added")
        }
        if (customerMap.size == 3) {
            val updateJobOffer: UJobOfferStateDTO = mapper.readValue(customerString, UJobOfferStateDTO::class.java)
            customerService.updateJobOffer(updateJobOffer)
            logger.info("Job offer Updated")
        }

    }

    @KafkaListener(
        id = "delete_customer",
        topics = ["delete_customer"],
        groupId = "analytics"
    )
    fun deleteCustomer(customerString: String) {
        val customer: DCustomerDTO = mapper.readValue(customerString, DCustomerDTO::class.java)
        logger.info("Received message on delete_customer topic")
        customerService.delete(customer)
        logger.info("Customer deleted")
    }

}