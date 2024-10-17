package org.example.analytics.services

import org.example.analytics.dtos.JobOfferDTO
import org.example.analytics.dtos.request.create.CJobOfferDTO
import org.example.analytics.dtos.request.delete.DJobOfferDTO
import org.example.analytics.dtos.request.update.UJobOfferDurationDTO
import org.example.analytics.dtos.request.update.UJobOfferValueDTO

interface JobOfferService {

    fun listAll(): List<JobOfferDTO>

    fun maxMinAverageValue(): Map<String, Any>

    fun addJobOffer(newJobOffer: CJobOfferDTO)

    fun updateJobOfferDuration(jobOfferDuration: UJobOfferDurationDTO)

    fun updateJobOfferValue(jobOfferValue: UJobOfferValueDTO)

    fun deleteJobOffer(jobOfferId: DJobOfferDTO)
}