package com.barostartbe.domain.auth.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank

data class LoginRequestDto(
    @Schema(description = "유저 로그인 id", example = "baro1234")
    @Max(value = 20)
    @NotBlank
    val loginId: String,

    @Schema(description = "유저 패스워드", example = "1234")
    @Max(value = 50)
    @NotBlank
    val password: String
)