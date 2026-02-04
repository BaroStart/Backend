package com.barostartbe.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.jsonMapper
import tools.jackson.module.kotlin.kotlinModule

@Configuration
class WebConfig {
    @Bean
    fun objectMapper(): JsonMapper = jsonMapper { addModules(kotlinModule()) }
}