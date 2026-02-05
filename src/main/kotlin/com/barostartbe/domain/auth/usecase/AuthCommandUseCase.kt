package com.barostartbe.domain.auth.usecase

import com.barostartbe.domain.auth.dto.SignupRequestDto
import com.barostartbe.domain.user.entity.User
import com.barostartbe.domain.user.repository.UserRepository
import com.barostartbe.global.annotation.CommandUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import org.springframework.security.crypto.password.PasswordEncoder

@CommandUseCase
class AuthCommandUseCase(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder
) {
    fun createUser(request: SignupRequestDto){
        if (userRepository.existsUserByLoginId(request.loginId)) {
            throw ServiceException(ErrorCode.DUPLICATED_LOGIN_ID)
        }
        val user = User.from(request)
        user.password = passwordEncoder.encode(request.password).toString()

        userRepository.save(user)
    }
}