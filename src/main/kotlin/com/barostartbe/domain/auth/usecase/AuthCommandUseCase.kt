package com.barostartbe.domain.auth.usecase

import com.barostartbe.domain.auth.dto.LoginRequestDto
import com.barostartbe.domain.auth.dto.SignupRequestDto
import com.barostartbe.domain.auth.dto.SignupResponseDto
import com.barostartbe.domain.auth.dto.TokenPairResponseDto
import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.domain.user.entity.AccessLog
import com.barostartbe.domain.user.repository.AccessLogRepository
import com.barostartbe.domain.user.repository.UserRepository
import com.barostartbe.global.annotation.CommandUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.crypto.password.PasswordEncoder

@CommandUseCase
class AuthCommandUseCase(
    val authQueryUseCase: AuthQueryUseCase,
    val userRepository: UserRepository,
    val accessLogRepository: AccessLogRepository,
    val passwordEncoder: PasswordEncoder
) {
    fun createUser(request: SignupRequestDto): SignupResponseDto{
        if (userRepository.existsUserByLoginId(request.loginId)) {
            throw ServiceException(ErrorCode.DUPLICATED_LOGIN_ID)
        }
        val user = when(request.joinType){
            "MENTOR" -> Mentor.from(request)
            "MENTEE" -> Mentee.from(request)
            else -> throw ServiceException(ErrorCode.BAD_PARAMETER)
        }
        user.password = passwordEncoder.encode(request.password).toString()

        val savedUser = userRepository.save(user)

        return SignupResponseDto(savedUser.id!!)
    }

    fun login(request: HttpServletRequest): TokenPairResponseDto {
        val requestDto: LoginRequestDto = request.getAttribute("requestBody") as LoginRequestDto

        val user = userRepository.findUserByLoginId(requestDto.loginId)
            ?: throw ServiceException(ErrorCode.USER_NOT_FOUND)

        saveAccessLog(user.id!!)

        return authQueryUseCase.generateTokenPairAndSaveRefreshTokenInRedis(user.loginId!!)
    }

    fun saveAccessLog(userId: Long){
        accessLogRepository.save(AccessLog.of(userId))
    }
}