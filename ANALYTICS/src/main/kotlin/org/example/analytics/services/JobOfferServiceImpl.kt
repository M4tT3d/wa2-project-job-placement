package org.example.analytics.services

import org.example.analytics.documents.JobOffer
import org.example.analytics.dtos.JobOfferDTO
import org.example.analytics.dtos.request.create.CJobOfferDTO
import org.example.analytics.dtos.request.delete.DJobOfferDTO
import org.example.analytics.dtos.request.update.UJobOfferDurationDTO
import org.example.analytics.dtos.request.update.UJobOfferValueDTO
import org.example.analytics.dtos.toDTO
import org.example.analytics.repositories.JobOfferRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class JobOfferServiceImpl(
    private val jobOfferRepository: JobOfferRepository
) : JobOfferService {

    override fun listAll(): List<JobOfferDTO> {
        return jobOfferRepository.findAll().map { it.toDTO() }
    }

    override fun maxMinAverageValue(): Map<String, Any> {
        val jobOffers = jobOfferRepository.findAll()
        if (jobOffers.isEmpty()) {
            return mapOf(
                "maxRate" to "none",
                "minRate" to "none",
                "averageRate" to "none"
            )
        } else {
            val maxRate = jobOffers.maxBy { it.value }
            val minRate = jobOffers.minBy { it.value }
            val averageRate = jobOffers.map { it.value }.average()
            return mapOf(
                "maxRate" to String.format("%.2f", maxRate.value),
                "minRate" to String.format("%.2f", minRate.value),
                "averageRate" to String.format("%.2f", averageRate)
            )
        }
    }

    override fun addJobOffer(newJobOffer: CJobOfferDTO) {
        jobOfferRepository.save(
            JobOffer(
                jobOfferId = newJobOffer.jobOfferId,
                value = newJobOffer.value,
                duration = newJobOffer.duration
            )
        )
    }

    override fun updateJobOfferDuration(jobOfferDuration: UJobOfferDurationDTO) {
        val jobOfferDB = jobOfferRepository.findByJobOfferId(jobOfferDuration.jobOfferId)
            ?: throw IllegalArgumentException("JobOffer not found")
        jobOfferDB.duration = jobOfferDuration.duration
        jobOfferRepository.save(jobOfferDB)
    }

    override fun updateJobOfferValue(jobOfferValue: UJobOfferValueDTO) {
        val jobOfferDB = jobOfferRepository.findByJobOfferId(jobOfferValue.jobOfferId)
            ?: throw IllegalArgumentException("JobOffer not found")
        jobOfferDB.value = jobOfferValue.value
        jobOfferRepository.save(jobOfferDB)
    }

    override fun deleteJobOffer(jobOfferId: DJobOfferDTO) {
        val jobOfferDB = jobOfferRepository.findByJobOfferId(jobOfferId.id)
        if (jobOfferDB != null) {
            jobOfferRepository.delete(jobOfferDB)
        }
    }
}
