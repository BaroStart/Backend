package com.barostartbe.domain.auth.dto

data class SignupRequestDto(
    val loginId: String,
    val password: String,
    val name: String,
    val nickname: String
)
