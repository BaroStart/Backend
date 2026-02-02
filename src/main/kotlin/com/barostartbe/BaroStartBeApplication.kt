package com.barostartbe

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class BaroStartBeApplication

fun main(args: Array<String>) {
    runApplication<BaroStartBeApplication>(*args)
}
