package org.example.crm.utils

const val PAGE_NUMBER: Long = 1
const val PAGE_SIZE: Long = 10
const val MOBILEPHONE_REGEX = "^[+]?[0-9]{1,4}?[-. ]?[(]?[0-9]{1,3}?[)]?[-. ]?[0-9]{1,4}[-. ]?[0-9]{1,9}$"
const val SSNCODE_REGEX = "[a-zA-Z0-9]+"
val DEFAULT_SKILLS = arrayOf(
    "Purchasing - Logistics - Transportation",
    "Legal Affairs",
    "Administration - Secretarial",
    "Architecture - Graphic Arts - Design",
    "Elderly and/or disabled care",
    "Commercial",
    "Trade - Stores",
    "Accounting - Finance",
    "Management - Consulting",
    "Construction",
    "Publishing - Journalism",
    "Aesthetics - Personal Care",
    "Training - Education",
    "Information Technology - Telecommunications",
    "Engineering",
    "Marketing - Communication",
    "Medicine - Health",
    "Production - Workers",
    "Project Management",
    "Quality - Environment",
    "Human Resources",
    "Security - Vigilance",
    "Customer Support",
    "Tourism - Catering",
    "Other - Domestic work"
)
const val PROFIT_MARGIN = 100.0f