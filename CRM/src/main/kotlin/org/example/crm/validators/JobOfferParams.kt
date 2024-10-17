package org.example.crm.validators

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import org.example.crm.dtos.request.filters.JobOfferParamsDTO
import org.example.crm.utils.enums.StateJobOffer
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [JobOfferParamsValidation::class])
@Target(AnnotationTarget.CLASS)
annotation class JobOfferParams(
    val message: String = "Invalid body",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class JobOfferParamsValidation : ConstraintValidator<JobOfferParams, JobOfferParamsDTO> {
    override fun isValid(jobOfferParams: JobOfferParamsDTO, context: ConstraintValidatorContext): Boolean {
        jobOfferParams.skills?.forEach {
            if (it < 0) return false
        }
        return when (jobOfferParams.jobOffer) {
            "open" -> {
                if (jobOfferParams.customerId == null) {
                    context.disableDefaultConstraintViolation()
                    context.buildConstraintViolationWithTemplate("Customer Id cannot be null")
                        .addPropertyNode("customer")
                        .addConstraintViolation()
                    return false
                }
                return true
            }

            "accepted" -> {
                if (jobOfferParams.professionalId == null) {
                    context.disableDefaultConstraintViolation()
                    context.buildConstraintViolationWithTemplate("Professional Id cannot be null")
                        .addPropertyNode("customer")
                        .addConstraintViolation()
                    return false
                }
                return true
            }
            null -> {
                return true
            }

            else -> {
                StateJobOffer.valueOf(jobOfferParams.jobOffer.uppercase())
                return true
            }
        }
    }
}