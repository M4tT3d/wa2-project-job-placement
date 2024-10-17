package org.example.crm.entities

import jakarta.persistence.*

@MappedSuperclass
abstract class BaseEntity(
    @Column(nullable = true)
    var comment: String? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long = 0
}