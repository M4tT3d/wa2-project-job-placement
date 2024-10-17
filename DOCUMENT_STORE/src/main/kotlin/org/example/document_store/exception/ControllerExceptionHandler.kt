package org.example.document_store.exception

import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ControllerExceptionHandler {
    private val logger = LoggerFactory.getLogger(ControllerExceptionHandler::class.java)

    @ExceptionHandler(DuplicateKeyException::class)
    fun handleDuplicateKeyException(e: DuplicateKeyException): ResponseEntity<ErrorMessage> {
        val er = ErrorMessage(
            statusCode = HttpStatus.BAD_REQUEST.value(),
            message = e.message,
            description = "Document with the same name already exists"
        )
        logger.info("Document with the same name already exists")
        return ResponseEntity(er, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(e: EntityNotFoundException): ResponseEntity<ErrorMessage> {
        val er = ErrorMessage(
            statusCode = HttpStatus.NOT_FOUND.value(),
            message = e.message,
            description = "Document with this id does not exist"
        )
        return ResponseEntity(er, HttpStatus.NOT_FOUND)
    }


}