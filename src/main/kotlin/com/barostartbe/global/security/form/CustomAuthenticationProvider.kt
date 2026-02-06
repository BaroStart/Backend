package com.barostartbe.global.security.form

import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder

class CustomAuthenticationProvider (
    val customUserDetailService: CustomUserDetailService,
    val passwordEncoder: PasswordEncoder
): AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val user = customUserDetailService.loadUserByUsername(authentication.name)

        return if (passwordEncoder.matches(authentication.credentials.toString(), user.password)){
            UsernamePasswordAuthenticationToken(user, "password", user.authorities)
        }else throw ServiceException(ErrorCode.PASSWORD_MISMATCH)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication.equals(UsernamePasswordAuthenticationToken::class.java)
    }
}