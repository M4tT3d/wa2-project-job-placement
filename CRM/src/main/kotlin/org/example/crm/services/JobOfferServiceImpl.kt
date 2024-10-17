package org.example.crm.services

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.example.crm.dtos.JobOfferDTO
import org.example.crm.dtos.request.create.CJobOfferDTO
import org.example.crm.dtos.request.filters.PaginationParams
import org.example.crm.dtos.request.update.UJobOfferDTO
import org.example.crm.dtos.toDto
import org.example.crm.entities.JobOffer
import org.example.crm.entities.Skill
import org.example.crm.repositories.*
import org.example.crm.utils.PROFIT_MARGIN
import org.example.crm.utils.enums.EmploymentState
import org.example.crm.utils.enums.StateJobOffer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.domain.Specification
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.stereotype.Service

@Service
@Transactional
@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
class JobOfferServiceImpl(
    private val jobOfferRepository: JobOfferRepository,
    private val professionalRepository: ProfessionalRepository,
    private val customerRepository: CustomerRepository,
    private val skillRepo: SkillRepository,
    private val contactRepository: ContactRepository,
) : JobOfferService {

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, String>
    private val logger = LoggerFactory.getLogger(JobOfferService::class.java)

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun create(createJobOfferDTO: CJobOfferDTO): JobOfferDTO {
        val customer = customerRepository.findById(createJobOfferDTO.customerId)
        if (!customer.isPresent) throw EntityNotFoundException("Customer with id ${createJobOfferDTO.customerId} not found")

        val skills: Set<Skill> =
            skillRepo.findAll().filter { createJobOfferDTO.skills.contains(it.id) }.toSet()
        val jobOffer = JobOffer(createJobOfferDTO, skills.toMutableSet(), customer.get())
        val joSkills: String =
            skillRepo.findAll()
                .filter { createJobOfferDTO.skills.contains(it.id) }
                .joinToString("; ") { "id:${it.id},name:${it.skill}" }
        try {
            val message = "{ \"joSkills\":\"${joSkills}\"}"
            kafkaTemplate.send("jo_skills", message)
            logger.info("kafka send message for new professional")
        } catch (e: Exception) {
            logger.error("Error while sending message to Kafka", e)
        }
        try {
            val message = " {\"customerId\":\"${jobOffer.customer.id}\", \"jobOfferNew\":\"true\"}"
            kafkaTemplate.send("contact_customer", message)
            logger.info("kafka send message for new customer")
        } catch (e: Exception) {
            logger.error("Error while sending message to Kafka", e)
        }
        val joDTO = jobOfferRepository.save(jobOffer).toDto()
        try {
            val message = " {\"jobOfferId\":\"${joDTO.id}\", \"value\":0, \"duration\":\"${joDTO.duration}\"}"
            kafkaTemplate.send("jo", message)
            logger.info("kafka send message for new job offer")
        } catch (e: Exception) {
            logger.error("Error while sending message to Kafka", e)
        }
        return joDTO
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun update(id: Long, uJobOfferDTO: UJobOfferDTO): JobOfferDTO {
        val jobOffer = jobOfferRepository.findById(id)
        if (jobOffer.isPresent) {

            val jobOfferToUpdate = jobOffer.get()
            if (jobOfferToUpdate.duration != uJobOfferDTO.duration) {
                try {
                    val message = " {\"jobOfferId\":\"${id}\", \"duration\":\"${uJobOfferDTO.duration}\"}"
                    kafkaTemplate.send("jo", message)
                    logger.info("kafka send message for updating job offer")
                } catch (e: Exception) {
                    logger.error("Error while sending message to Kafka", e)
                }
            }
            jobOfferToUpdate.description = uJobOfferDTO.description ?: jobOffer.get().description
            jobOfferToUpdate.status = jobOffer.get().status

            val oldSkills = jobOfferToUpdate.skills.map { it.id }
            if (oldSkills != uJobOfferDTO.skills) {
                val oldS = oldSkills.joinToString("-") { it.toString() }
                val newSkill = skillRepo.findAll()
                    .filter { uJobOfferDTO.skills?.contains(it.id) ?: false }
                    .joinToString("; ") { "id:${it.id},name:${it.skill}" }
                try {
                    val message = "{ \"oldSkills\":\"${oldS}\",\"newSkills\":\"${newSkill}\"}"
                    kafkaTemplate.send("jo_skills", message)
                    logger.info("kafka send message for update skills of job offer")
                } catch (e: Exception) {
                    logger.error("Error while sending message to Kafka", e)
                }

            }
            jobOfferToUpdate.skills = if (!uJobOfferDTO.skills.isNullOrEmpty())
                skillRepo.findAll().filter { uJobOfferDTO.skills.contains(it.id) }.toSet()
                    .toMutableSet() else jobOffer.get().skills
            jobOfferToUpdate.comment = uJobOfferDTO.comment ?: jobOffer.get().comment
            jobOfferToUpdate.duration = uJobOfferDTO.duration ?: jobOffer.get().duration
            jobOfferToUpdate.customer =
                if (uJobOfferDTO.customerId == 0L) customerRepository.findById(uJobOfferDTO.customerId)
                    .get() else jobOffer.get().customer


            return jobOfferRepository.save(jobOfferToUpdate).toDto()
        } else {
            throw EntityNotFoundException("This id (${id}) does not exist")
        }

    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun getById(id: Long): JobOfferDTO {
        val jobOffer = jobOfferRepository.findById(id)
        if (jobOffer.isPresent) {
            return jobOffer.get().toDto()
        } else {
            throw EntityNotFoundException("This id (${id}) does not exist")
        }
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun updateState(
        jobOfferId: Long, state: StateJobOffer, professionalId: Long?, employmentState: EmploymentState?
    ): JobOfferDTO {
        val jobOffer = jobOfferRepository.findById(jobOfferId)
        val jobOfferToUpdate = jobOffer.get()
        if (professionalId != null) {

            val professional = professionalRepository.findById(professionalId)
            if (!professional.isPresent) throw EntityNotFoundException("Professional with id $professionalId not found")

            if (professional.get().employmentState == EmploymentState.NOT_AVAILABLE || (professional.get().employmentState == EmploymentState.EMPLOYED && jobOfferToUpdate.status != StateJobOffer.CONSOLIDATED)) throw IllegalArgumentException(
                "Professional is not available"
            )

            val value = calculateValue(jobOfferToUpdate.duration, professional.get().dailyRate)
            if (jobOfferToUpdate.status == StateJobOffer.SELECTION_PHASE) {
                professional.get().jobOffers?.add(jobOfferToUpdate)
                jobOffer.get().value = 0.0f
            } else {
                if (professionalId != jobOffer.get().professional!!.id) throw IllegalArgumentException("Professional id is not the same as the one in the job offer")
                if (state == StateJobOffer.ABORTED) {
                    professional.get().jobOffers?.remove(jobOfferToUpdate)
                }
            }
            if (employmentState != null) {
                try {
                    val message =
                        "{ \"employmentState_old\":\"${professional.get().employmentState}\", \"employmentState_new\":\"${employmentState}\"}"
                    kafkaTemplate.send("contact_professional", message)
                    logger.info("kafka send message for new professional")
                } catch (e: Exception) {
                    logger.error("Error while sending message to Kafka", e)
                }
                professional.get().employmentState = employmentState
            }
            professionalRepository.save(professional.get())
            jobOfferToUpdate.professional = professional.get()
            jobOfferToUpdate.value = value
            try {
                val message = " {\"jobOfferId\":\"${jobOfferId}\", \"value\":${value}}"
                kafkaTemplate.send("jo", message)
                logger.info("kafka send message for updating value of job offer")
            } catch (e: Exception) {
                logger.error("Error while sending message to Kafka", e)
            }
        }
        try {
            val message =
                " {\"customerId\":\"${jobOfferToUpdate.customer.id}\", \"jobOfferState_old\":\"${jobOfferToUpdate.status}\", \"jobOfferState_new\":\"${state}\"}"
            kafkaTemplate.send("contact_customer", message)
            logger.info("kafka send message for new customer")
        } catch (e: Exception) {
            logger.error("Error while sending message to Kafka", e)
        }
        jobOfferToUpdate.status = state
        return jobOfferRepository.save(jobOfferToUpdate).toDto()

    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun listAll(
        paginationParams: PaginationParams,
        specs: Specification<JobOffer>,
    ): List<JobOfferDTO> {

        return jobOfferRepository.findAll(specs, paginationParams.toPageRequest()).content.map { it.toDto() }
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun deleteByCustomer(idCustomer: Long?) {
        if (idCustomer == null) return
        val jobOffers = jobOfferRepository.findByCustomerId(idCustomer)

        jobOffers.forEach {
            val professional = it.professional
            if (professional != null) {
                val prof = professionalRepository.findById(professional.id).get()
                try {
                    val message =
                        "{ \"employmentState_old\":\"${prof.employmentState}\", \"employmentState_new\":\"${EmploymentState.UNEMPLOYED_AVAILABLE}\"}"
                    kafkaTemplate.send("contact_professional", message)
                    logger.info("kafka send message for new professional")
                } catch (e: Exception) {
                    logger.error("Error while sending message to Kafka", e)
                }
                prof.jobOffers?.remove(it)
                prof.employmentState = EmploymentState.UNEMPLOYED_AVAILABLE
                professionalRepository.save(prof)
            }
            try {
                val message =
                    " {\"id\":\"${it.id}\"}"
                kafkaTemplate.send("jo", message)
                logger.info("kafka send message for delete job offer")
            } catch (e: Exception) {
                logger.error("Error while sending message to Kafka", e)
            }
            jobOfferRepository.delete(it)
        }
    }

    @PreAuthorize("authentication.principal.claims['roles'].contains('admin')||authentication.principal.claims['roles'].contains('operator')")
    override fun deleteByProfessional(idProfessional: Long?) {
        if (idProfessional == null) return
        val jobOffers = jobOfferRepository.findByProfessionalId(idProfessional)
        jobOffers.forEach {
            try {
                val message =
                    " {\"customerId\":\"${it.customer.id}\", \"jobOfferState_old\":\"${it.status}\", \"jobOfferState_new\":\"${StateJobOffer.SELECTION_PHASE}\"}"
                kafkaTemplate.send("contact_customer", message)
                logger.info("kafka send message for new customer")
            } catch (e: Exception) {
                logger.error("Error while sending message to Kafka", e)
            }
            it.professional = null
            it.status = StateJobOffer.SELECTION_PHASE
            jobOfferRepository.save(it)
        }
    }

    fun calculateValue(duration: Float, professionalRate: Float): Float {
        return duration * professionalRate * PROFIT_MARGIN
    }

    override fun getRows(specs: Specification<JobOffer>): Int {
        return jobOfferRepository.findAll(specs).size
    }
}





