package com.barostartbe.global.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfig : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**").apply {
            allowedMethods("OPTIONS", "HEAD", "GET", "POST", "PUT", "PATCH", "DELETE")
            allowCredentials(true)
            allowedOrigins("http://localhost:3000")
            allowedHeaders("*")
        }
    }
}
