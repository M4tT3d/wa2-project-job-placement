package org.example.communicationmanager.dtos

data class CMailDTO(
    val email: String,
    val subject: String,
    val body: String,
) {
    init {
        val emailRegex = "^[^@]+@[^@]+\\.[^@]+$".toRegex()
        if (!emailRegex.matches(email))
            throw IllegalArgumentException("Invalid email address")
    }

}