package org.example.crm.integrationTest.services

import jakarta.persistence.EntityNotFoundException
import org.example.crm.dtos.SkillDTO
import org.example.crm.integrationTest.controllers.IntegrationTest
import org.example.crm.services.SkillService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ContextConfiguration(initializers = [IntegrationTest.Initializer::class])
class SkillServiceIntegrationTest : IntegrationTest(){
    @Autowired
    private lateinit var skillService: SkillService

    private lateinit var skill: SkillDTO

    @BeforeEach
    fun setUp() {
        skill = skillService.addSkill("Engineering")
    }

    @Test
    fun testCreateNewSkill() {
        val skillString = "Engineering"
        val createdSkill = skillService.addSkill(skillString)

        Assertions.assertNotNull(createdSkill)
        Assertions.assertEquals(skillString,createdSkill.skill)
    }

    @Test
    fun testGetSkills() {
        val skills = skillService.getSkills()
        Assertions.assertNotNull(skills)
        Assertions.assertTrue(skills.isNotEmpty())
    }

    @Test
    fun testGetSkillById() {
        val skillById = skillService.getSkillById(skill.id)
        Assertions.assertNotNull(skillById)
        Assertions.assertEquals(skill.skill, skillById.skill)
    }

    @Test
    fun testUpdateSkill() {
        val newSkill = "Software Engineering"
        val updatedSkill = skillService.updateSkill(skill.id, newSkill)
        Assertions.assertNotNull(updatedSkill)
        Assertions.assertEquals(newSkill, updatedSkill.skill)
    }

    @Test
    fun testDeleteSkill() {
        skillService.deleteSkill(skill.id)
        val skills = skillService.getSkills()

        assertThrows<EntityNotFoundException> {
            skillService.getSkillById(skill.id)
        }
    }
}