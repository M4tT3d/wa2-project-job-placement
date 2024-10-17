package org.example.iam

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.security.web.csrf.*
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.util.function.Supplier

@Configuration
class SecurityConfig(val crr: ClientRegistrationRepository) {
    fun oidcLogoutSuccessHandler() = OidcClientInitiatedLogoutSuccessHandler(crr)
        .also { it.setPostLogoutRedirectUri("http://localhost:8080/") }

    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        return httpSecurity
            .authorizeHttpRequests {
                it.requestMatchers("/", "/login", "/logout").permitAll()
                it.requestMatchers("/ui/**", "/me").permitAll()
                it.requestMatchers("/secure").authenticated()
                it.requestMatchers("/crm/**").authenticated()
                it.requestMatchers("/documentstore/**").authenticated()
                it.requestMatchers("/communicationmanager/**").authenticated()
                it.requestMatchers("/analytics/**").authenticated()
                it.requestMatchers("/actuator/**").permitAll()
                it.requestMatchers("/**").authenticated()
            }
            .oauth2Login { }
            .csrf {
                it.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                it.csrfTokenRequestHandler(SpaCsrfTokenRequestHandler())
            }
            .logout { it.logoutSuccessHandler(oidcLogoutSuccessHandler()) }
            .addFilterAfter(CsrfCookieFilter(), BasicAuthenticationFilter::class.java)
            .build()
    }
}

class SpaCsrfTokenRequestHandler : CsrfTokenRequestAttributeHandler() {
    private val delegate: CsrfTokenRequestHandler = CsrfTokenRequestAttributeHandler()

    override fun handle(req: HttpServletRequest, res: HttpServletResponse, t: Supplier<CsrfToken>) {
        delegate.handle(req, res, t)
    }

    override fun resolveCsrfTokenValue(request: HttpServletRequest, csrfToken: CsrfToken): String? {
        val d = csrfToken as DefaultCsrfToken
        return if (StringUtils.hasText(request.getHeader(csrfToken.headerName))) super.resolveCsrfTokenValue(
            request,
            csrfToken
        )
        else delegate.resolveCsrfTokenValue(request, csrfToken)
    }
}
class CsrfCookieFilter : OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val csrfToken = request.getAttribute("_csrf") as CsrfToken
        csrfToken.token
        chain.doFilter(request, response)
    }
}
