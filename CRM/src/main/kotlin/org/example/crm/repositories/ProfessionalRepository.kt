package org.example.crm.repositories

import org.example.crm.entities.Professional
import org.springframework.data.jpa.repository.JpaRepository

interface ProfessionalRepository : JpaRepository<Professional, Long>