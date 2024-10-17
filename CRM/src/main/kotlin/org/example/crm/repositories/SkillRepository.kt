package org.example.crm.repositories

import org.example.crm.entities.Skill
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface SkillRepository : JpaRepository<Skill, Long>, JpaSpecificationExecutor<Skill>