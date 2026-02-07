package com.barostartbe.domain.auth.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank

@Schema(description = "로그인 및 회원가입 시 전달되는 자격 정보")
data class LoginRequestDto(
    @Schema(description = "유저 로그인 id", example = "baro1234")
    @Max(value = 20)
    val loginId: String?,

    @Schema(description = "유저 패스워드", example = "1234")
    @Max(value = 50)
    val password: String?
)