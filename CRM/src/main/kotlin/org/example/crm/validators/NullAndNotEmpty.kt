package org.example.crm.validators

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [StringValidator::class, CollectionValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
annotation class NullAndNotEmpty(
    val message: String = "Field cannot be empty",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class StringValidator : ConstraintValidator<NullAndNotEmpty, String?> {
    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true
        return value.trim().isNotEmpty()
    }
}

class CollectionValidator : ConstraintValidator<NullAndNotEmpty, Collection<*>> {
    override fun isValid(value: Collection<*>?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true
        return value.isNotEmpty()
    }
}