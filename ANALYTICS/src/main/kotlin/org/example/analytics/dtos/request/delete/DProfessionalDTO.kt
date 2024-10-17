package org.example.analytics.dtos.request.delete

import org.example.analytics.utils.EmploymentState

data class DProfessionalDTO(
    val deleteState: EmploymentState,
    val deleteSkills: String,
)
