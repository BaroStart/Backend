package com.barostartbe.domain.auth.dto

data class TokenPairResponseDto(
    val userId: Long?,
    val accessToken: String,
    val refreshToken: String
)
