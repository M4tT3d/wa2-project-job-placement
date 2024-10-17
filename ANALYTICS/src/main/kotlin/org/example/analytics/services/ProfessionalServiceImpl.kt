package org.example.analytics.services

import org.example.analytics.documents.Professional
import org.example.analytics.dtos.ProfessionalDTO
import org.example.analytics.dtos.request.create.CProfessionalDTO
import org.example.analytics.dtos.request.delete.DProfessionalDTO
import org.example.analytics.dtos.request.update.UProfessionalDTO
import org.example.analytics.dtos.toDTO
import org.example.analytics.repositories.ProfessionalRepository
import org.example.analytics.utils.EmploymentState
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProfessionalServiceImpl(
    private val professionalRepository: ProfessionalRepository
) : ProfessionalService {

    override fun getProfessional(): ProfessionalDTO {
        val result = professionalRepository.findAll().firstOrNull()
            ?: return ProfessionalDTO(
                id = null,
                employed = 0,
                unemployed_available = 0,
                not_available = 0,
                rateAverage = 0,
            )
        return result.toDTO()
    }

    override fun create(newProfessional: CProfessionalDTO): ProfessionalDTO {
        var professionalDB = professionalRepository.findAll().firstOrNull()
        if (professionalDB == null) {
            professionalDB = Professional(
                employed = 0,
                unemployed_available = 0,
                not_available = 0,
                rateAverage = 0,
            )
        }
        when (newProfessional.employmentState) {
            EmploymentState.EMPLOYED -> professionalDB.employed += 1
            EmploymentState.UNEMPLOYED_AVAILABLE -> professionalDB.unemployed_available += 1
            EmploymentState.NOT_AVAILABLE -> professionalDB.not_available += 1
        }
        return professionalRepository.save(professionalDB).toDTO()
    }

    override fun update(updateProfessional: UProfessionalDTO): ProfessionalDTO {
        val professionalDB = professionalRepository.findAll().firstOrNull()
        if (professionalDB != null) {
            when {
                updateProfessional.employmentState_old == EmploymentState.EMPLOYED && updateProfessional.employmentState_new == EmploymentState.UNEMPLOYED_AVAILABLE -> {
                    professionalDB.employed -= 1
                    professionalDB.unemployed_available += 1
                }

                updateProfessional.employmentState_old == EmploymentState.EMPLOYED && updateProfessional.employmentState_new == EmploymentState.NOT_AVAILABLE -> {
                    professionalDB.employed -= 1
                    professionalDB.not_available += 1
                }

                updateProfessional.employmentState_old == EmploymentState.UNEMPLOYED_AVAILABLE && updateProfessional.employmentState_new == EmploymentState.EMPLOYED -> {
                    professionalDB.unemployed_available -= 1
                    professionalDB.employed += 1
                }

                updateProfessional.employmentState_old == EmploymentState.UNEMPLOYED_AVAILABLE && updateProfessional.employmentState_new == EmploymentState.NOT_AVAILABLE -> {
                    professionalDB.unemployed_available -= 1
                    professionalDB.not_available += 1
                }

                updateProfessional.employmentState_old == EmploymentState.NOT_AVAILABLE && updateProfessional.employmentState_new == EmploymentState.EMPLOYED -> {
                    professionalDB.not_available -= 1
                    professionalDB.employed += 1
                }

                updateProfessional.employmentState_old == EmploymentState.NOT_AVAILABLE && updateProfessional.employmentState_new == EmploymentState.UNEMPLOYED_AVAILABLE -> {
                    professionalDB.not_available -= 1
                    professionalDB.unemployed_available += 1
                }
            }
            return professionalRepository.save(professionalDB).toDTO()
        }
        return ProfessionalDTO(
            id = null,
            employed = 0,
            unemployed_available = 0,
            not_available = 0,
            rateAverage = 0,
        )
    }

    override fun delete(deleteProfessional: DProfessionalDTO) {
        val professionalDB = professionalRepository.findAll().firstOrNull()
        if (professionalDB != null) {
            when (deleteProfessional.deleteState) {
                EmploymentState.EMPLOYED -> professionalDB.employed -= 1
                EmploymentState.UNEMPLOYED_AVAILABLE -> professionalDB.unemployed_available -= 1
                EmploymentState.NOT_AVAILABLE -> professionalDB.not_available -= 1
            }
            professionalRepository.save(professionalDB)
        }
    }
}