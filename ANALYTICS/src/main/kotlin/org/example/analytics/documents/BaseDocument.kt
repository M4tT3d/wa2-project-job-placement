package org.example.analytics.documents


import org.springframework.data.annotation.Id

abstract class BaseDocument {
    @Id
    var id: String? = null
}