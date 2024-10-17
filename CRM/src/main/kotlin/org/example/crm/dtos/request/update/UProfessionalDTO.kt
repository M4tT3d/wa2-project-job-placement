package org.example.crm.dtos.request.update

import org.example.crm.utils.enums.EmploymentState
import org.example.crm.validators.NullAndNotEmpty

data class UProfessionalDTO(
    @field:NullAndNotEmpty(message = "Skills cannot be empty")
    val skills: Set<Long>? = null,
    val employmentState: EmploymentState? = null,
    @field:NullAndNotEmpty(message = "Location cannot be empty")
    val location: String? = null,
    val dailyRate: Float? = null,
    @field:NullAndNotEmpty(message = "Comment cannot be empty")
    val comment: String? = null
)
