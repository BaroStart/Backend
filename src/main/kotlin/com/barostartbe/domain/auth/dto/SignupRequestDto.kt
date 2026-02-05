package com.barostartbe.domain.auth.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank

data class SignupRequestDto(
    @Schema(description = "로그인 id", example = "baro1234")
    @Max(value = 20)
    @NotBlank
    val loginId: String,

    @Schema(description = "패스워드", example = "1234")
    @Max(value = 50)
    @NotBlank
    val password: String,

    @Schema(description = "이름", example = "baro")
    @Max(value = 10)
    @NotBlank
    val name: String,

    @Schema(description = "닉네임", example = "baro1234")
    @Max(value = 30)
    @NotBlank
    val nickname: String,

    @Schema(description = "가입 유형", example = "MENTOR")
    @NotBlank
    val joinType: String,

    @Schema(description = "멘티 학년", example = "FIRST")
    val grade: String?,

    @Schema(description = "멘티 학교", example = "NORMAL")
    val school: String?,

    @Schema(description = "멘티 희망전공", example = "medical")
    val hopeMajor: String?,

    @Schema(description = "멘토 대학", example = "서울대")
    val university: String?
)
