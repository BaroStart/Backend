package com.barostartbe.domain.auth.usecase

import com.barostartbe.domain.auth.dto.SignupRequestDto
import com.barostartbe.domain.user.entity.User
import com.barostartbe.domain.user.repository.UserRepository
import com.barostartbe.global.annotation.CommandUseCase
import org.springframework.security.crypto.password.PasswordEncoder

@CommandUseCase
class AuthCommandUseCase(
    val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun createUser(request: SignupRequestDto){
        if (userRepository.existsUserByLoginId(request.loginId)) {
            return
        }
        val user = User.from(request)
        user.password = passwordEncoder.encode(request.password).toString()

        userRepository.save(user)
    }
}