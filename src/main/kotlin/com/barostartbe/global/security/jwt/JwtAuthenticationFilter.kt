package com.barostartbe.global.security.jwt

import com.barostartbe.domain.user.repository.UserRepository
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    val jwtUtil: JwtUtil,
    val userRepository: UserRepository,
    val redisTemplate: RedisTemplate<String, Any>
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val accessToken: String = request.getHeader("Authorization")
            ?.takeIf { it.startsWith("Bearer") }
            ?.removePrefix("Bearer ")
            ?: return filterChain.doFilter(request, response)

        val claim = jwtUtil.getClaimsFromToken(accessToken)

        if (isLogoutToken(claim.id)) return filterChain.doFilter(request, response)

        val user = userRepository.findUserByLoginId(claim.subject)
            ?: throw ServiceException(ErrorCode.USER_NOT_FOUND)

        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(user,  "password", user.authorities)

        filterChain.doFilter(request, response)
    }

    fun isLogoutToken(tokenId: String): Boolean = redisTemplate.hasKey("redis::logout::$tokenId")

}