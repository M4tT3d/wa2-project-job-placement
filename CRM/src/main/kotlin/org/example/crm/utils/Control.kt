package org.example.crm.utils

fun isEmail(input: String): Boolean {
    val emailRegex = "^[^@]+@[^@]+\\.[^@]+$".toRegex()
    return emailRegex.matches(input)
}

fun isPhoneNumber(input: String): Boolean {
    val phoneRegex = SSNCODE_REGEX.toRegex()
    return phoneRegex.matches(input)
}