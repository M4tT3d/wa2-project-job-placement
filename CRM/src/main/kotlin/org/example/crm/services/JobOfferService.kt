package org.example.crm.services

import org.example.crm.dtos.JobOfferDTO
import org.example.crm.dtos.request.create.CJobOfferDTO
import org.example.crm.dtos.request.filters.ContactParams
import org.example.crm.dtos.request.filters.PaginationParams
import org.example.crm.dtos.request.update.UJobOfferDTO
import org.example.crm.entities.JobOffer
import org.example.crm.utils.enums.EmploymentState
import org.example.crm.utils.enums.StateJobOffer
import org.springframework.data.jpa.domain.Specification

interface JobOfferService {
    fun create(createJobOfferDTO: CJobOfferDTO): JobOfferDTO

    fun update(id: Long, uJobOfferDTO: UJobOfferDTO): JobOfferDTO

    fun getById(id: Long): JobOfferDTO

    fun updateState(
        jobOfferId: Long,
        state: StateJobOffer,
        professionalId: Long?,
        employmentState: EmploymentState?
    ): JobOfferDTO

    fun deleteByCustomer(idCustomer: Long?)

    fun deleteByProfessional(idProfessional: Long?)

    fun listAll(
        paginationParams: PaginationParams,
        specs: Specification<JobOffer>
    ): List<JobOfferDTO>

    fun getRows(
        specs: Specification<JobOffer>
    ): Int

}