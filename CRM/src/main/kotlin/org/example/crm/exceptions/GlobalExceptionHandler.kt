package org.example.crm.exceptions

import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

class InvalidStateException(message: String) : RuntimeException(message)


@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)


    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ErrorMessage> {
        val em = ErrorMessage(
            statusCode = 400,
            message = "Bad request",
            description = e.message ?: "Bad request"
        )
        logger.error("Bad request: ${e.message}")
        return ResponseEntity(em, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(e: DataIntegrityViolationException): ResponseEntity<ErrorMessage> {
        val em = ErrorMessage(
            statusCode = 400,
            message = "Bad request",
            description = e.message ?: "Bad request"
        )
        logger.error("Bad request: ${e.message}")
        return ResponseEntity(em, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidStateException::class)
    fun handleInvalidStateException(
        e: InvalidStateException,
    ): ResponseEntity<ErrorMessage> {
        val er = ErrorMessage(
            statusCode = HttpStatus.BAD_REQUEST.value(),
            message = e.message,
            description = "State error"
        )
        return ResponseEntity(er, HttpStatus.BAD_REQUEST)
    }
}