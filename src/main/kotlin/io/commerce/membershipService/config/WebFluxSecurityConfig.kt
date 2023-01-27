package io.commerce.membershipService.config

import io.commerce.membershipService.core.CustomReactiveOpaqueTokenIntrospector
import io.commerce.membershipService.core.SecurityConstants
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebFluxSecurity
class WebFluxSecurityConfig(
    private val properties: OAuth2ResourceServerProperties
) {
    private val corsConfig = CorsConfiguration().apply {
        allowedOrigins = listOf("*")
        allowedHeaders = listOf("*")
        allowedMethods = listOf("GET", "HEAD", "POST", "PUT", "DELETE")
    }

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http {
            csrf { disable() }

            authorizeExchange {
                authorize("/membership/**", hasAuthority(SecurityConstants.CUSTOMER))
                authorize("/admin/**", hasAuthority(SecurityConstants.SERVICE_ADMIN))
                authorize("/**", permitAll)
            }
            oauth2ResourceServer {
                opaqueToken { }
            }
        }
    }

    @Bean
    fun corsConfigurationSource() = UrlBasedCorsConfigurationSource().apply {
        registerCorsConfiguration("/**", corsConfig)
    }

    @Bean
    fun introspector() = CustomReactiveOpaqueTokenIntrospector(properties.opaquetoken)
}
