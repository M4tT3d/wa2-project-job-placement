package org.example.analytics.dtos.request.update

import org.example.analytics.utils.EmploymentState

data class UProfessionalDTO(
    val employmentState_old: EmploymentState,
    val employmentState_new: EmploymentState
)
