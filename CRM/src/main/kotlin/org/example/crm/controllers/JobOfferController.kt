package org.example.crm.controllers

import jakarta.validation.Valid
import org.example.crm.dtos.request.create.CJobOfferDTO
import org.example.crm.dtos.request.filters.JobOfferParamsDTO
import org.example.crm.dtos.request.filters.PaginationParams
import org.example.crm.dtos.request.update.UJobOfferDTO
import org.example.crm.dtos.request.update.UStateJobOfferDTO
import org.example.crm.dtos.toResponse
import org.example.crm.services.JobOfferService
import org.example.crm.utils.enums.StateJobOffer
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.MissingRequestValueException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/joboffers")
class JobOfferController(
    val jobOfferService: JobOfferService,
) {
    private val logger = LoggerFactory.getLogger(JobOfferController::class.java)

    @PostMapping("/", "")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: CJobOfferDTO): ResponseEntity<Map<String, Any?>> {
        logger.info("Creating job offer ")
        val response = jobOfferService.create(
            request
        ).toResponse()
        logger.info("Job offer created with id: ${response["id"]}")
        return ResponseEntity(response, HttpStatus.CREATED)
    }

    @PutMapping("/{jobOfferId}/", "/{jobOfferId}")
    @ResponseStatus(HttpStatus.OK)
    fun updateJobOffer(
        @Valid @RequestBody request: UJobOfferDTO,
        @PathVariable jobOfferId: Long
    ): ResponseEntity<Map<String, Any?>> {
        logger.info("Editing job offer ")
        val response = jobOfferService.update(
            jobOfferId,
            request
        ).toResponse()
        logger.info("Job offer updated")
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PostMapping("/{jobOfferId}/", "/{jobOfferId}")
    @ResponseStatus(HttpStatus.OK)
    fun updateState(
        @Valid @RequestBody request: UStateJobOfferDTO,
        @PathVariable jobOfferId: Long
    ): ResponseEntity<Map<String, Any?>> {
        logger.info("Editing state for job offer ")

        val jobOffer = jobOfferService.getById(jobOfferId)
        val nextState = jobOffer.status.getNextState()

        val newJobOffer: Any

        if (nextState.contains(request.status)) {
            when (request.status) {
                StateJobOffer.ABORTED -> {
                    if (jobOffer.status == StateJobOffer.CONSOLIDATED && request.professionalId == null) {
                        logger.error("Professional id is required")
                        throw MissingRequestValueException("Professional id is required")
                    }

                    newJobOffer = jobOfferService.updateState(
                        jobOfferId,
                        request.status,
                        request.professionalId,
                        if (request.professionalId != null) request.status.getEmploymentState()!! else null
                    )
                }

                StateJobOffer.SELECTION_PHASE -> {
                    newJobOffer = jobOfferService.updateState(
                        jobOfferId,
                        request.status,
                        null,
                        null
                    )
                }

                StateJobOffer.CANDIDATE_PROPOSAL -> {
                    if (request.professionalId == null) {
                        //logger.error("Professional id is required")
                        throw MissingRequestValueException("Professional id is required")
                    }

                    newJobOffer = jobOfferService.updateState(
                        jobOfferId,
                        request.status,
                        request.professionalId,
                        null
                    )
                }

                StateJobOffer.CONSOLIDATED -> {

                    newJobOffer = jobOfferService.updateState(
                        jobOfferId,
                        request.status,
                        request.professionalId,
                        request.status.getEmploymentState()!!
                    )
                }

                StateJobOffer.DONE -> {
                    newJobOffer = jobOfferService.updateState(
                        jobOfferId,
                        request.status,
                        request.professionalId,
                        request.status.getEmploymentState()!!
                    )
                }

                else -> {
                    logger.error("Invalid state for jobOffer")
                    return ResponseEntity(
                        mapOf(
                            "error" to "Invalid state for jobOffer",
                        ), HttpStatus.BAD_REQUEST
                    )
                }
            }
            logger.info("Job offer state updated")
            return ResponseEntity(newJobOffer.toResponse(), HttpStatus.OK)
        } else {
            //logger.error("Invalid state for jobOffer")
            throw IllegalArgumentException("Invalid state for jobOffer")
        }
    }

    @GetMapping("/{jobOfferId}/value", "/{jobOfferId}/value/")
    @ResponseStatus(HttpStatus.OK)
    fun getValue(@PathVariable jobOfferId: Long): ResponseEntity<Map<String, Any?>> {

        val jobOffer = jobOfferService.getById(jobOfferId)
        if (jobOffer.professional == null) {
            return ResponseEntity(
                mapOf(
                    "error" to "No professional associated with this job offer",
                ), HttpStatus.BAD_REQUEST
            )
        } else {
            return ResponseEntity(
                mapOf(
                    "value" to jobOffer.value,
                ), HttpStatus.OK
            )
        }
    }

    @GetMapping("/{jobOfferId}/", "/{jobOfferId}")
    fun findByJobOfferId(@PathVariable jobOfferId: Long): ResponseEntity<Map<String, Any?>> {
        val jobOffer = jobOfferService.getById(jobOfferId).toResponse()
        val possibleStatus= jobOfferService.getById(jobOfferId).status.getNextState()
        val response = mapOf(
            "jobOffer" to jobOffer,
            "possibleStatuses" to possibleStatus
        )
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping("/", "")
    fun listAll(
        @Validated paginationParams: PaginationParams,
        @Validated params: JobOfferParamsDTO,
        authentication: Authentication
    ): ResponseEntity<Map<String, Any?>> {
        val specs = params.toSpecification()
        val principal = authentication.principal as Jwt
        /* val issuerUri = "http://localhost:9090/realms/IAM"
         val principal = authentication.principal as Jwt
         val jwtDecoder = JwtDecoders.fromIssuerLocation<JwtDecoder>(issuerUri)
         val tokenValue = principal.tokenValue

         val jwt: Jwt = jwtDecoder.decode(tokenValue)

         val claims = jwt.claims
         logger.error(claims["roles"].toString())
         if(claims["roles"].toString().contains("admin")){*/
        logger.error(principal.claims.toString())
        val response = jobOfferService.listAll(
            paginationParams,
            specs
        ).map { it.toResponse() }
        val rows = jobOfferService.getRows(specs)
        return ResponseEntity(mapOf("jobOffers" to response, "rowCount" to rows), HttpStatus.OK)
    }
}