package com.barostartbe.domain.auth.controller

import com.barostartbe.domain.auth.dto.LoginRequestDto
import com.barostartbe.domain.auth.dto.SignupRequestDto
import com.barostartbe.domain.auth.dto.SignupResponseDto
import com.barostartbe.domain.auth.dto.TokenPairResponseDto
import com.barostartbe.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "Auth API", description = "로그인, 로그아웃 관리 api")
@RequestMapping("/api/v1")
interface AuthApi {
    @PostMapping("/login")
    @Operation(summary = "로그인 api", description = "로그인에 성공하면 access/refresh 토큰을 반환한다.")
    fun login(request: HttpServletRequest, @io.swagger.v3.oas.annotations.parameters.RequestBody body: LoginRequestDto): ResponseEntity<ApiResponse<TokenPairResponseDto>>

    @PostMapping("/signup")
    @Operation(summary = "회원가입 api", description = "회원 정보를 db에 저장한다.")
    fun signup(@RequestBody request: SignupRequestDto): ResponseEntity<ApiResponse<SignupResponseDto>>

    @GetMapping("/logout")
    @Operation(summary = "로그아웃 api", description = "회원의 access 토큰을 무효화시킨다.")
    fun logout(request: HttpServletRequest): ResponseEntity<ApiResponse<String>>

    @GetMapping("/refresh")
    @Operation(summary = "토큰 리프레시 api", description = "access 토큰과 refresh 토큰을 재발급한다.")
    fun refresh(@RequestParam token: String): ResponseEntity<ApiResponse<TokenPairResponseDto>>
}