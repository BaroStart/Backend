package com.barostartbe.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {

        http {
            httpBasic { disable() }
            formLogin { disable() }
            cors { }
            csrf { disable() }
            sessionManagement { SessionCreationPolicy.STATELESS }

        }

        http {
            authorizeHttpRequests {

                // CORS preflight
                authorize(HttpMethod.OPTIONS, "/**", permitAll)

                // swagger
                authorize("/v3/api-docs/**", permitAll)
                authorize("/swagger-ui/**", permitAll)

                //example
                authorize("/api/v1/examples/**", permitAll)

                // default
                authorize(anyRequest, denyAll)
            }
        }

        return http.build()
    }
}
