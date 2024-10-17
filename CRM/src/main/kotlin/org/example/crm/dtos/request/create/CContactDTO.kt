package org.example.crm.dtos.request.create

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.example.crm.utils.SSNCODE_REGEX
import org.example.crm.utils.enums.Category
import org.example.crm.validators.ContactBody


@ContactBody
data class CContactDTO(
    @field:NotBlank(message = "Name cannot be empty")
    val name: String,
    @field:NotBlank(message = "Surname cannot be empty")
    val surname: String,
    @field:Size(min = 16, max = 16, message = "SSN code length must be 16 characters")
    @field:Pattern(regexp = SSNCODE_REGEX, message = "Invalid SSN code")
    val ssnCode: String? = null,
    val category: Category,
    val comment: String? = null,
    @field:Valid
    val emails: List<CEmailDTO>? = null,
    @field:Valid
    val addresses: List<CAddressDTO>? = null,
    @field:Valid
    val telephones: List<CTelephoneDTO>? = null,
    @field:Valid
    val professional: CProfessionalDTO? = null,
    @field:Valid
    val customer: CCustomerDTO? = null
)