package org.example.communicationmanager.exceptions

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

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
}