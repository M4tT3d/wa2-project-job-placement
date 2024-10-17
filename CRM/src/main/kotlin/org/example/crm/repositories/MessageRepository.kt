package org.example.crm.repositories

import org.example.crm.entities.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository


@Repository
interface MessageRepository : JpaRepository<Message, Long>, JpaSpecificationExecutor<Message>