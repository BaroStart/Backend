package com.barostartbe.global.security.form

import com.barostartbe.domain.auth.dto.LoginRequestDto
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AuthenticationConverter
import org.springframework.security.web.authentication.AuthenticationFilter
import tools.jackson.databind.json.JsonMapper
import java.util.stream.Collectors

class CustomAuthenticationFilter(
    val customAuthenticationManager: CustomAuthenticationManager,
    val objectMapper: JsonMapper,
    authenticationConverter: AuthenticationConverter
) : AuthenticationFilter(customAuthenticationManager, authenticationConverter){

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        if (
            request.requestURI.equals("/api/v1/login", ignoreCase = true) &&
            request.method.equals("POST", ignoreCase = true)
        )
        {
            val requestBody = request.reader.lines().collect(Collectors.joining(System.lineSeparator()))

            try
            {
                val loginRequestDto = objectMapper.readValue(requestBody, LoginRequestDto::class.java)
                request.setAttribute("requestBody", loginRequestDto)

                val authentication = customAuthenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(loginRequestDto.loginId, loginRequestDto.password, listOf<GrantedAuthority>())
                )
                SecurityContextHolder.getContext().authentication = authentication
            }
            catch (ex: Exception){
                if (ex is ServiceException) throw ex
                throw ServiceException(ErrorCode.INTERNAL_SERVER_ERROR)
            }
        }

        filterChain.doFilter(request, response)
    }
}