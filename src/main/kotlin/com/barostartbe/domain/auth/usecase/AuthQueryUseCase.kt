package com.barostartbe.domain.auth.usecase

import com.barostartbe.domain.auth.dto.TokenPairResponseDto
import com.barostartbe.domain.user.repository.UserRepository
import com.barostartbe.global.annotation.QueryUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import com.barostartbe.global.security.jwt.JwtUtil
import com.barostartbe.global.security.jwt.enum.TokenType
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.redis.core.RedisTemplate
import java.time.Duration

@QueryUseCase
class AuthQueryUseCase(
    val userRepository: UserRepository,
    val jwtUtil: JwtUtil,
    val redisTemplate: RedisTemplate<String, Any>
){

    fun regenerateToken(refreshToken: String): TokenPairResponseDto{
        val claims = jwtUtil.getClaimsFromToken(refreshToken);

        val token = redisTemplate.opsForValue().get("redis::refresh::${claims.subject}").toString()

        return if (token.equals(refreshToken)){

            generateTokenPairAndSaveRefreshTokenInRedis(claims.subject)

        }else throw ServiceException(ErrorCode.INVALID_TOKEN)

    }

    fun generateTokenPairAndSaveRefreshTokenInRedis(loginId: String): TokenPairResponseDto{
        val user = userRepository.findUserByLoginId(loginId)
            ?: throw ServiceException(ErrorCode.USER_NOT_FOUND)

        val access = jwtUtil.generateToken(TokenType.ACCESS, user)
        val refresh = jwtUtil.generateToken(TokenType.REFRESH, user)

        saveRefreshTokenInRedis(loginId, refresh)

        return TokenPairResponseDto(access, refresh)
    }

    fun saveRefreshTokenInRedis(loginId: String, refreshToken: String){
        redisTemplate.opsForValue().set("redis::refresh::${loginId}", refreshToken)
    }

    fun logout(request: HttpServletRequest){
        val access = request.getHeader("Authorization").substringAfter("Bearer ")

        val claims = jwtUtil.getClaimsFromToken(access)

        redisTemplate.delete("redis::refresh::${claims.subject}")
        redisTemplate.opsForValue().set("redis::logout::${claims.id}", true, Duration.ofMinutes(30))
    }
}
