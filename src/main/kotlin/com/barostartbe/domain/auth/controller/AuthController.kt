package com.barostartbe.domain.auth.controller

import com.barostartbe.domain.auth.dto.TokenPairResponseDto
import com.barostartbe.domain.auth.dto.SignupRequestDto
import com.barostartbe.domain.auth.usecase.AuthCommandUseCase
import com.barostartbe.domain.auth.usecase.AuthQueryUseCase
import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.response.type.SuccessCode
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class AuthController(
    val authCommandUseCase: AuthCommandUseCase,
    val authQueryUseCase: AuthQueryUseCase
) {
    @PostMapping("/login")
    fun login(request: HttpServletRequest): ResponseEntity<ApiResponse<TokenPairResponseDto>> = ApiResponse.success(SuccessCode.REQUEST_OK, authQueryUseCase.login(request))

    @PostMapping("/signup")
    fun signup(@RequestBody request: SignupRequestDto): ResponseEntity<ApiResponse<String>> {
        authCommandUseCase.createUser(request)
        return ApiResponse.success(SuccessCode.CREATE_OK, "회원가입 되었습니다.")
    }
}