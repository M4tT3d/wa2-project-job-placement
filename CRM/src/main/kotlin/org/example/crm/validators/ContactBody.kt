package org.example.crm.validators

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import org.example.crm.dtos.request.create.CContactDTO
import org.example.crm.dtos.request.update.UContactDTO
import org.example.crm.utils.enums.Category
import kotlin.reflect.KClass


@MustBeDocumented
@Constraint(validatedBy = [CBodyValidator::class, UBodyValidator::class])
@Target(AnnotationTarget.CLASS)
annotation class ContactBody(
    val message: String = "Invalid body",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class CBodyValidator : ConstraintValidator<ContactBody, CContactDTO> {
    override fun isValid(requestBody: CContactDTO, context: ConstraintValidatorContext): Boolean {
        return when (requestBody.category) {
            Category.CUSTOMER -> {
                if (requestBody.customer == null) {
                    context.disableDefaultConstraintViolation()
                    context.buildConstraintViolationWithTemplate("Customer data cannot be null")
                        .addPropertyNode("customer")
                        .addConstraintViolation()
                    return false
                }
                return true
            }

            Category.PROFESSIONAL -> {
                if (requestBody.professional == null) {
                    context.disableDefaultConstraintViolation()
                    context.buildConstraintViolationWithTemplate("Professional data cannot be null")
                        .addPropertyNode("professional")
                        .addConstraintViolation()
                    return false
                }
                if (requestBody.professional.dailyRate <= 0f) {
                    context.disableDefaultConstraintViolation()
                    context.buildConstraintViolationWithTemplate("Rate must be greater than 0")
                        .addPropertyNode("rate")
                        .addConstraintViolation()
                    return false
                }
                return true
            }

            Category.UNKNOWN -> true
        }
    }
}

class UBodyValidator : ConstraintValidator<ContactBody, UContactDTO> {
    override fun isValid(requestBody: UContactDTO, context: ConstraintValidatorContext): Boolean {
        if (requestBody.category == null) return true
        return when (requestBody.category) {
            Category.CUSTOMER -> {
                if (requestBody.customer == null) {
                    context.disableDefaultConstraintViolation()
                    context.buildConstraintViolationWithTemplate("Customer data cannot be null")
                        .addPropertyNode("customer")
                        .addConstraintViolation()
                    return false
                }
                return true
            }

            Category.PROFESSIONAL -> {
                if (requestBody.professional == null) {
                    context.disableDefaultConstraintViolation()
                    context.buildConstraintViolationWithTemplate("Professional data cannot be null")
                        .addPropertyNode("professional")
                        .addConstraintViolation()
                    return false
                }
                if (requestBody.professional.dailyRate == null) return true
                if (requestBody.professional.dailyRate <= 0f) {
                    context.disableDefaultConstraintViolation()
                    context.buildConstraintViolationWithTemplate("Rate must be greater than 0")
                        .addPropertyNode("rate")
                        .addConstraintViolation()
                    return false
                }
                return true
            }

            Category.UNKNOWN -> true
        }
    }
}