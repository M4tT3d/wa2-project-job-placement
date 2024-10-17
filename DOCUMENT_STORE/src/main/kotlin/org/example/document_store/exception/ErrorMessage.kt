package org.example.document_store.exception

import org.springframework.http.HttpStatus

class ErrorMessage(
    val statusCode: Int? = HttpStatus.INTERNAL_SERVER_ERROR.value(),
    val message: String?,
    val description: String
) {}