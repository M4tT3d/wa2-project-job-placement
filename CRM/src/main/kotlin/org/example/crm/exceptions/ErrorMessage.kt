package org.example.crm.exceptions

import org.springframework.http.HttpStatus

class ErrorMessage(
    val statusCode: Int? = HttpStatus.INTERNAL_SERVER_ERROR.value(),
    val message: String?,
    val description: String
)