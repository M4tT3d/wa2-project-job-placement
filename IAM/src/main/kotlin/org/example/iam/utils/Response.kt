package org.example.iam.utils

import java.time.LocalDateTime

data class Response(
    var statusCode: Int? = null,
    var message: String? = null,
    var executionMessage: String? = null,
    var responseTime: LocalDateTime? = null
)