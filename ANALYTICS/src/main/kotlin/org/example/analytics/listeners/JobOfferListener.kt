package org.example.analytics.listeners

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.example.analytics.dtos.request.create.CJobOfferDTO
import org.example.analytics.dtos.request.delete.DJobOfferDTO
import org.example.analytics.dtos.request.update.UJobOfferDurationDTO
import org.example.analytics.dtos.request.update.UJobOfferValueDTO
import org.example.analytics.services.JobOfferService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class JobOfferListener(
    val jobOfferService: JobOfferService
) {
    private val logger = LoggerFactory.getLogger(CustomerListener::class.java)
    private val mapper = jacksonObjectMapper()

    @KafkaListener(
        id = "jo",
        topics = ["jo"],
        groupId = "analytics"
    )
    fun jobOffer(jobOfferString: String) {
        val jobOfferMap: Map<String, Any> = mapper.readValue(jobOfferString)
        logger.info("Received message on jo topic")
        if (jobOfferMap.size == 1) {
            val jobOffer = mapper.readValue(jobOfferString, DJobOfferDTO::class.java)
            jobOfferService.deleteJobOffer(jobOffer)
            logger.info("Job offer deleted")
        } else if (jobOfferMap.size == 2) {
            if (jobOfferMap.containsKey("duration")) {
                val jobOffer = mapper.readValue(jobOfferString, UJobOfferDurationDTO::class.java)
                jobOfferService.updateJobOfferDuration(jobOffer)
                logger.info("Job offer duration updated")
            } else {
                val jobOffer = mapper.readValue(jobOfferString, UJobOfferValueDTO::class.java)
                jobOfferService.updateJobOfferValue(jobOffer)
                logger.info("Job offer value updated")
            }

        } else if (jobOfferMap.size == 3) {
            val jobOffer = mapper.readValue(jobOfferString, CJobOfferDTO::class.java)
            jobOfferService.addJobOffer(jobOffer)
            logger.info("Job offer added")

        }
    }
}