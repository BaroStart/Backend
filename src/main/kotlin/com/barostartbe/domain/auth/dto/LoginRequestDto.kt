package com.barostartbe.domain.auth.dto

data class LoginRequestDto(
    val loginId: String,
    val password: String
)