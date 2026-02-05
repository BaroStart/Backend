package com.barostartbe.domain.auth.controller

import com.barostartbe.domain.auth.dto.TokenPairResponseDto
import com.barostartbe.domain.auth.dto.SignupRequestDto
import com.barostartbe.domain.auth.usecase.AuthCommandUseCase
import com.barostartbe.domain.auth.usecase.AuthQueryUseCase
import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.response.type.SuccessCode
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    val authCommandUseCase: AuthCommandUseCase,
    val authQueryUseCase: AuthQueryUseCase,
) : AuthApi {

    override fun login(request: HttpServletRequest): ResponseEntity<ApiResponse<TokenPairResponseDto>> = ApiResponse.success(SuccessCode.REQUEST_OK, authCommandUseCase.login(request))

    override fun signup(@RequestBody request: SignupRequestDto): ResponseEntity<ApiResponse<String>> {
        authCommandUseCase.createUser(request)
        return ApiResponse.success(SuccessCode.CREATE_OK, "회원가입 되었습니다.")
    }

    override fun logout(request: HttpServletRequest): ResponseEntity<ApiResponse<String>> {
        authQueryUseCase.logout(request)
        return ApiResponse.success(SuccessCode.REQUEST_OK, "로그아웃 되었습니다.")
    }
}