package org.example.crm.integrationTest.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.crm.entities.Skill
import org.example.crm.repositories.SkillRepository
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.*

@SpringBootTest
@AutoConfigureMockMvc
@Sql("/sql/contactData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class SkillControllerIntegration : IntegrationTest(){
    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    lateinit var skillRepo: SkillRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `addSkkill should return status CREATED`(){
        val newSkill = mapOf("skill" to "New Skill")

        val skillJson = objectMapper.writeValueAsString(newSkill)
        mockMvc.post("/api/skills"){
            contentType = MediaType.APPLICATION_JSON
            content = skillJson
        }.andExpect {
            status { isCreated() }
            jsonPath("$.skill", Matchers.notNullValue())
            jsonPath("$.skill.skill", Matchers.`is`("New Skill"))
        }
    }

    @Test
    fun `addSkill should return status BAD_REQUEST`(){
        val newSkill = mapOf("skill" to "")

        val skillJson = objectMapper.writeValueAsString(newSkill)
        mockMvc.post("/api/skills"){
            contentType = MediaType.APPLICATION_JSON
            content = skillJson
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.message", Matchers.`is`("Validation error"))
            jsonPath("$.statusCode", Matchers.`is`(400))
            jsonPath("$.description", Matchers.`is`("Skill cannot be empty"))
        }
    }

    @Test
    fun `getSkills should return status OK`(){
        mockMvc.get("/api/skills"){
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.skills", Matchers.notNullValue())
        }
    }

    @Test
    fun `getSkillById should return status OK`(){
        val skill = skillRepo.save(Skill("Engineering"))
        mockMvc.get("/api/skills/${skill.id}"){
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.skill", Matchers.notNullValue())
            jsonPath("$.skill.skill", Matchers.`is`("Engineering"))
        }
    }

    @Test
    fun `getSkillById should return status NOT_FOUND`(){
        val skill = skillRepo.save(Skill("Engineering"))
        skillRepo.delete(skill)
        mockMvc.get("/api/skills/${skill.id}"){
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.message", Matchers.`is`("Entity not found"))
            jsonPath("$.statusCode", Matchers.`is`(404))
            jsonPath("$.description", Matchers.`is`("Skill not found"))
        }
    }

    @Test
    fun `updateSkill should return status OK`(){
        val skill = skillRepo.save(Skill("Engineering"))
        val updatedSkill = mapOf("skill" to "Updated Skill")

        val skillJson = objectMapper.writeValueAsString(updatedSkill)
        mockMvc.put("/api/skills/${skill.id}"){
            contentType = MediaType.APPLICATION_JSON
            content = skillJson
        }.andExpect {
            status { isOk() }
            jsonPath("$.skill", Matchers.notNullValue())
            jsonPath("$.skill.skill", Matchers.`is`("Updated Skill"))
        }
    }

    @Test
    fun `updateSkill should return status BAD_REQUEST`(){
        val skill = skillRepo.save(Skill("Engineering"))
        val updatedSkill = mapOf("skill" to "")

        val skillJson = objectMapper.writeValueAsString(updatedSkill)
        mockMvc.put("/api/skills/${skill.id}"){
            contentType = MediaType.APPLICATION_JSON
            content = skillJson
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.message", Matchers.`is`("Validation error"))
            jsonPath("$.statusCode", Matchers.`is`(400))
            jsonPath("$.description", Matchers.`is`("Skill cannot be empty"))
        }
    }

    @Test
    fun `deleteSkill should return status NO_CONTENT`(){
        val skill = skillRepo.save(Skill("Engineering"))
        mockMvc.delete("/api/skills/${skill.id}"){
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNoContent() }
        }
    }

    @Test
    fun `deleteSkill should return status NOT_FOUND`(){
        val skill = skillRepo.save(Skill("Engineering"))
        skillRepo.delete(skill)
        mockMvc.delete("/api/skills/${skill.id}"){
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.message", Matchers.`is`("Entity not found"))
            jsonPath("$.statusCode", Matchers.`is`(404))
            jsonPath("$.description", Matchers.`is`("Skill not found"))
        }
    }

}