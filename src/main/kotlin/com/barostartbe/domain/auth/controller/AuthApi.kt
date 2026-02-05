package com.barostartbe.domain.auth.controller

import com.barostartbe.domain.auth.dto.LoginRequestDto
import com.barostartbe.domain.auth.dto.SignupRequestDto
import com.barostartbe.domain.auth.dto.TokenPairResponseDto
import com.barostartbe.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Auth API", description = "로그인, 로그아웃 관리 api")
@RequestMapping("/api/v1")
interface AuthApi {
    @PostMapping("/login")
    @Operation(summary = "로그인 api", description = "로그인에 성공하면 access/refresh 토큰을 반환한다.")
    fun login(request: HttpServletRequest, @io.swagger.v3.oas.annotations.parameters.RequestBody body: LoginRequestDto): ResponseEntity<ApiResponse<TokenPairResponseDto>>

    @PostMapping("/signup")
    @Operation(summary = "회원가입 api", description = "회원 정보를 db에 저장한다.")
    fun signup(@RequestBody request: SignupRequestDto): ResponseEntity<ApiResponse<String>>
}