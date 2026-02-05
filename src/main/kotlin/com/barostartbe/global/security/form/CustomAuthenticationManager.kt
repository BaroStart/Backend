package com.barostartbe.global.security.form

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication

class CustomAuthenticationManager(
    val customAuthenticationProvider: CustomAuthenticationProvider
) : AuthenticationManager {

    override fun authenticate(authentication: Authentication): Authentication {
        return customAuthenticationProvider.authenticate(authentication)
    }
}