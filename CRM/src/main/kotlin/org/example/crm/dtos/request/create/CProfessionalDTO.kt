package org.example.crm.dtos.request.create

import jakarta.validation.constraints.NotEmpty
import org.example.crm.utils.enums.EmploymentState
import org.example.crm.validators.NullAndNotEmpty

data class CProfessionalDTO(
    @field:NotEmpty(message = "Skills cannot be empty")
    val skills: Set<Long> = setOf(),
    val employmentState: EmploymentState,
    @field:NullAndNotEmpty(message = "Location cannot be empty")
    val location: String? = null,
    val dailyRate: Float,
    @field:NullAndNotEmpty(message = "Comment cannot be empty")
    val comment: String? = null
)
