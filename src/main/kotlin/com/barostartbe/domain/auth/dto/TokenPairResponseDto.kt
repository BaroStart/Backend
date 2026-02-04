package com.barostartbe.domain.auth.dto

data class TokenPairResponseDto(
    val accessToken: String,
    val refreshToken: String
)
