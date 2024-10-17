package org.example.iam

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.servlet.http.HttpServletRequest
import org.example.iam.utils.Response
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.security.Principal
import java.time.LocalDateTime

@RestController
class HomeController(
    private val authorizedClientService: OAuth2AuthorizedClientService // Inietta il servizio per ottenere il token

) {

    @GetMapping("", "/")
    fun home(principal: Principal?): Map<String, Any?> {
        return mapOf(
            "name" to LocalDateTime.now(),
            "principal" to principal
        )
    }

    @GetMapping("/secure")
    fun secure(): Map<String, Any?> {
        val authentication = SecurityContextHolder.getContext().authentication
        return mapOf(
            "name" to "secure",
            "date" to LocalDateTime.now(),
            "principal" to authentication.principal
        )
    }

    @GetMapping("/me")
    fun me(
        @CookieValue(name = "XSRF-TOKEN", required = false) xsrf: String?,
        auth: Authentication?
    ): Map<String, Any?> {

        val principal: OidcUser? = auth?.principal as? OidcUser
        val username = principal?.preferredUsername

        return mapOf(
            "fullName" to principal?.fullName,
            "username" to username,
            "loginUrl" to "/oauth2/authorization/IAMclient",
            "logoutUrl" to "/logout",
            "xsrfToken" to xsrf,
            "principal" to principal
        )
    }


    @PostMapping("/upload-file",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    fun sendMultipartFileToDocumentstore(
        principal: Principal?,
        request: HttpServletRequest,
        @CookieValue(name = "XSRF-TOKEN", required = false) xsrfToken: String?,
        @RequestParam("file") file: MultipartFile,
        @RequestParam("contact_id") contactId: String

    ): ResponseEntity<String>
    {
        val oauth2Token = principal as? OAuth2AuthenticationToken
            ?: return ResponseEntity.status(401).body("User is not authenticated")

        val authorizedClient: OAuth2AuthorizedClient = authorizedClientService.loadAuthorizedClient(
            oauth2Token.authorizedClientRegistrationId,
            oauth2Token.name
        ) ?: return ResponseEntity.status(401).body("Could not retrieve the OAuth2 token")

        val jwtToken = authorizedClient.accessToken?.tokenValue
            ?: return ResponseEntity.status(401).body("No JWT token found for the current user")
        return ResponseEntity.ok(sendMultipartFile(file, contactId, "http://localhost:8084/api/documents/", xsrfToken ?: "", jwtToken))

    }

    fun sendMultipartFile(
        file: MultipartFile,
        contactId: String,
        apiValue: String,
        xsrfToken: String,
        jwtToken: String): String {
        val returnResponse = Response()
        val restTemplate = RestTemplate()

        try {
            val converter = MappingJackson2HttpMessageConverter()
            converter.supportedMediaTypes = listOf(MediaType.MULTIPART_FORM_DATA)
            restTemplate.messageConverters.add(converter)

            val builder = LinkedMultiValueMap<String, Any>()
            builder.add("file", file.resource)
            builder.add("contact_id", contactId)


            val headers = HttpHeaders().apply {
                contentType = MediaType.MULTIPART_FORM_DATA
                set("X-XSRF-TOKEN", xsrfToken)
                set("Authorization", "Bearer $jwtToken")
            }
            val requestEntity: HttpEntity<MultiValueMap<String, Any>> = HttpEntity(builder, headers)
            LoggerFactory.getLogger("APIGatewayController").info("requestEntity: $requestEntity")

            val resp = restTemplate.exchange(
                apiValue, HttpMethod.POST, requestEntity, Response::class.java
            )

            val apiResponse = resp.body
            val apiStausCode = resp.statusCode
            val apiHeaders = resp.headers
            LoggerFactory.getLogger("APIGatewayController").info("Getting response from API: $apiValue...")
            if (apiResponse != null) {
                LoggerFactory.getLogger("APIGatewayController").info("Response from api: $apiValue is fetched successfully!")
                return resp.body.toString()
            } else {
                LoggerFactory.getLogger("APIGatewayController").info("Response of API: [value: $apiValue, statusCode: $apiStausCode, headers: $apiHeaders] is coming null. Please Check!")
            }

        } catch (exception: Exception) {
            LoggerFactory.getLogger("APIGatewayController").error("Failed to call API: [$apiValue], Reason is: ${exception.message}")
            returnResponse.responseTime = LocalDateTime.now()
            returnResponse.statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value()
            returnResponse.message = "Internal Server Error"
            returnResponse.executionMessage = apiValue
        }
        return returnResponse.toString()
    }
}