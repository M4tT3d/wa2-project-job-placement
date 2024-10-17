package org.example.analytics.services

import org.example.analytics.dtos.ProfessionalDTO
import org.example.analytics.dtos.request.create.CProfessionalDTO
import org.example.analytics.dtos.request.delete.DProfessionalDTO
import org.example.analytics.dtos.request.update.UProfessionalDTO

interface ProfessionalService {

    fun getProfessional(): ProfessionalDTO

    fun create(newProfessional: CProfessionalDTO): ProfessionalDTO

    fun update(updateProfessional: UProfessionalDTO): ProfessionalDTO

    fun delete(deleteProfessional: DProfessionalDTO)

}