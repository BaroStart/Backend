package com.barostartbe.global.annotation

import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Service
@Transactional
annotation class CommandUseCase(@get:AliasFor(annotation = Service::class, attribute = "value") val value: String = "")
