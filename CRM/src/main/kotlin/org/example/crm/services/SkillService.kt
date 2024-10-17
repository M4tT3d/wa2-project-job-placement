package org.example.crm.services

import org.example.crm.dtos.SkillDTO

interface SkillService {
    fun addSkill(newSkill: String): SkillDTO
    fun getSkills(): List<SkillDTO>
    fun getSkillById(id: Long): SkillDTO
    fun updateSkill(id: Long, newSkill: String): SkillDTO
    fun deleteSkill(id: Long)
}