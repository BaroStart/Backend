package com.barostartbe.global.security.form

import com.barostartbe.domain.user.repository.UserRepository
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailService(
    val userRepository: UserRepository
): UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.findUserByLoginId(username)
             ?: throw ServiceException(ErrorCode.USER_NOT_FOUND)
    }
}