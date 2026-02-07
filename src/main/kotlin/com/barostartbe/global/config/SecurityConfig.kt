package com.barostartbe.global.config

import com.barostartbe.domain.user.repository.UserRepository
import com.barostartbe.global.security.form.CustomAuthenticationFilter
import com.barostartbe.global.security.form.CustomAuthenticationManager
import com.barostartbe.global.security.form.CustomAuthenticationProvider
import com.barostartbe.global.security.form.CustomUserDetailService

import com.barostartbe.global.security.jwt.JwtAuthenticationFilter
import com.barostartbe.global.security.jwt.JwtUtil
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationConverter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.www.BasicAuthenticationConverter
import tools.jackson.databind.json.JsonMapper

@Configuration
@EnableWebSecurity
class SecurityConfig(
    val jwtUtil: JwtUtil,
    val userRepository: UserRepository,
    val userDetailService: CustomUserDetailService,
    val objectMapper: JsonMapper,
    val redisTemplate: RedisTemplate<String, Any>
    ) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun customAuthenticationManager(): CustomAuthenticationManager = CustomAuthenticationManager(customAuthenticationProvider())

    @Bean
    fun customAuthenticationProvider(): CustomAuthenticationProvider = CustomAuthenticationProvider(userDetailService, passwordEncoder())

    @Bean
    fun authenticationConverter() : AuthenticationConverter = BasicAuthenticationConverter()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {

        val whitelist = listOf(
            "/api/v1/login", "/api/v1/signup", "/api/v1/refresh/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/api/sse/**",

            // 임시
            "/api/v1/assignments/**",
            "/api/v1/storages/**",
            "/api/v1/files/**"


        )

        http {
            httpBasic { disable() }
            formLogin { disable()}
            cors { }
            csrf { disable() }
            sessionManagement { SessionCreationPolicy.STATELESS }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(JwtAuthenticationFilter(jwtUtil, userRepository, redisTemplate))
            addFilterBefore<JwtAuthenticationFilter>(CustomAuthenticationFilter(customAuthenticationManager(), objectMapper, authenticationConverter()))
        }

        http {
            authorizeHttpRequests {

                // CORS preflight
                authorize(HttpMethod.OPTIONS, "/**", permitAll)

                whitelist.forEach { pattern ->
                    authorize(pattern, permitAll)
                }

                authorize ("/mentor/**", hasRole("MENTOR"))
                authorize ("/mentee/**", hasRole("MENTEE"))
                authorize ("/admin/**", hasRole("ADMIN"))

                // default
                authorize(anyRequest, authenticated)
            }
        }

        return http.build()
    }
}
