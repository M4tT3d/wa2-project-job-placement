package org.example.crm.dtos.request.update

import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.example.crm.dtos.request.create.CAddressDTO
import org.example.crm.dtos.request.create.CEmailDTO
import org.example.crm.dtos.request.create.CTelephoneDTO
import org.example.crm.utils.SSNCODE_REGEX
import org.example.crm.utils.enums.Category
import org.example.crm.validators.ContactBody
import org.example.crm.validators.NullAndNotEmpty

@ContactBody
data class UContactDTO(
    @field:NullAndNotEmpty(message = "Name cannot be empty")
    val name: String? = null,
    @field:NullAndNotEmpty(message = "Surname cannot be empty")
    val surname: String? = null,
    @field:Size(min = 16, max = 16, message = "SSN code length must be 16 characters")
    @field:Pattern(regexp = SSNCODE_REGEX, message = "Invalid SSN code")
    val ssnCode: String? = null,
    val category: Category? = null,
    @field:NullAndNotEmpty(message = "Comment cannot be empty")
    val comment: String? = null,
    @field:Valid
    val emails: List<CEmailDTO>? = null,
    @field:Valid
    val addresses: List<CAddressDTO>? = null,
    @field:Valid
    val telephones: List<CTelephoneDTO>? = null,
    @field:Valid
    val professional: UProfessionalDTO? = null,
    @field:Valid
    val customer: UCustomerDTO? = null
)