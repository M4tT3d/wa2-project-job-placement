package org.example.crm

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@SpringBootApplication
class CrmApplication {
    @Bean
    fun professionalTopic() = NewTopic("contact_professional", 1, 1)

    @Bean
    fun customerTopic() = NewTopic("contact_customer", 1, 1)

    @Bean
    fun joSkillsTopic() = NewTopic("jo_skills", 1, 1)

    @Bean
    fun pSkillsTopic() = NewTopic("p_skills", 1, 1)

    @Bean
    fun skillsTopic() = NewTopic("u_skills", 1, 1)

    @Bean
    fun joTopic() = NewTopic("jo", 1, 1)

    @Bean
    fun deleteCTopic() = NewTopic("delete_customer", 1, 1)

    @Bean
    fun deletePTopic() = NewTopic("delete_professional", 1, 1)

}

fun main(args: Array<String>) {
    runApplication<CrmApplication>(*args)
}

@Configuration
class RestTemplateConfig {

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}