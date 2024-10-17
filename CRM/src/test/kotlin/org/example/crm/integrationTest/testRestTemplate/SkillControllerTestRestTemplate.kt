package org.example.crm.integrationTest.testRestTemplate

import org.example.crm.entities.Skill
import org.example.crm.integrationTest.controllers.IntegrationTest
import org.example.crm.repositories.SkillRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SkillControllerTestRestTemplate : IntegrationTest(){
    @Autowired
    lateinit var restTemplate: TestRestTemplate
    @Autowired
    lateinit var skillRepo: SkillRepository

    private lateinit var skill: Skill

    @BeforeEach
    fun setUp() {
        skill = skillRepo.save(Skill("Engineering"))
    }

    @Test
    fun getSkills() {
        val res: ResponseEntity<Map<String, Any>> =
            restTemplate.getForEntity("/api/skills")
        assert(res.statusCode == HttpStatus.OK)
        assert(res.hasBody())
        println("Body: ${res.body}")
    }

    @Test
    fun getSkillByIdFailed() {
        skillRepo.delete(skill)
        val res: ResponseEntity<Map<String, Any>> =
            restTemplate.getForEntity("/api/skills/${skill.id}")
        assert(res.statusCode == HttpStatus.NOT_FOUND)
        assert(res.hasBody())
        println("Body: ${res.body}")
    }

    @Test
    fun addSkill() {
        val res: ResponseEntity<Map<String, Any>> =
            restTemplate.postForEntity("/api/skills", mapOf("skill" to "Java"))
        assert(res.statusCode == HttpStatus.CREATED)
        assert(res.hasBody())
        assert(res.body?.get("skill") != null)
        println("Body: ${res.body}")
    }

    @Test
    fun getSkillById() {
        val res: ResponseEntity<Map<String, Any>> =
            restTemplate.getForEntity("/api/skills/${skill.id}")
        assert(res.statusCode == HttpStatus.OK)
        assert(res.hasBody())
        println("Body: ${res.body}")
    }



    @Test
    fun updateSkill() {
        val updatedSkill = mapOf("skill" to "Updated Skill")

        val requestEntity = HttpEntity(updatedSkill, HttpHeaders())
        val res: ResponseEntity<Map<String, Any>> = restTemplate.exchange(
            "/api/skills/${skill.id}",
            HttpMethod.PUT,
            requestEntity,
            object : ParameterizedTypeReference<Map<String, Any>>() {}
        )

        assert(res.statusCode == HttpStatus.OK)
        assert(res.hasBody())
        println("Body: ${res.body}")
    }

    @Test
    fun delete() {
        val requestEntity = HttpEntity(null, HttpHeaders())
        val res: ResponseEntity<Map<String, Any>> =
            restTemplate.exchange("/api/skills/${skill.id}",
                HttpMethod.DELETE,
                requestEntity,
                object : ParameterizedTypeReference<Map<String, Any>>() {})
        assert(res.statusCode == HttpStatus.NO_CONTENT)
    }

}