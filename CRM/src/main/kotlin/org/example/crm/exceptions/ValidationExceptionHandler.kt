package org.example.crm.exceptions

import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestValueException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class ValidationExceptionHandler {
    private val logger = LoggerFactory.getLogger(ValidationExceptionHandler::class.java)

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(e: EntityNotFoundException): ResponseEntity<ErrorMessage> {
        val em = ErrorMessage(
            statusCode = 404,
            message = "Entity not found",
            description = e.message ?: "Entity not found"
        )
        logger.error("Entity not found: ${e.message}")
        return ResponseEntity(em, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleInvalidTransitionException(
        e: HttpMessageNotReadableException,
        request: WebRequest
    ): ResponseEntity<ErrorMessage> {
        val er = ErrorMessage(
            statusCode = HttpStatus.BAD_REQUEST.value(),
            message = e.message,
            description = "State error"
        )
        return ResponseEntity(er, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(e: MethodArgumentNotValidException): ResponseEntity<ErrorMessage> {
        val errors = e.bindingResult.allErrors.map { it.defaultMessage ?: "unknown error" }
        val em = ErrorMessage(
            statusCode = HttpStatus.BAD_REQUEST.value(),
            message = "Validation error",
            description = errors.joinToString(", ")
        )
        logger.error("Validation error: ${errors.joinToString(", ")}")
        return ResponseEntity(em, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MissingRequestValueException::class)
    fun handleMissingRequestValueException(e: MissingRequestValueException): ResponseEntity<ErrorMessage> {
        val em = ErrorMessage(
            statusCode = HttpStatus.BAD_REQUEST.value(),
            message = "Missing request value",
            description = e.message ?: "Missing request value"
        )
        logger.error("Missing request value: ${e.message}")
        return ResponseEntity(em, HttpStatus.BAD_REQUEST)
    }
}