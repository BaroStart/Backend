package com.barostartbe.global.security.jwt

import com.barostartbe.domain.user.entity.User
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import com.barostartbe.global.security.jwt.enum.TokenType
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Component
class JwtUtil (
    @Value("\${jwt.secret}") val secret: String,
    @Value("\${jwt.issuer}") val issuer: String,
    @Value("\${jwt.token.expire}") val tokenExpire: String,
    @Value("\${jwt.refresh.expire}") val refreshTokenExpire: String
){
    @OptIn(ExperimentalTime::class)
    fun generateToken(tokenType: TokenType, user: User): String{
        val expire: Long = when(tokenType) {
            TokenType.ACCESS -> tokenExpire.toLong()
            TokenType.REFRESH -> refreshTokenExpire.toLong()
        }

        return Jwts.builder()
            .issuer(issuer)
            .issuedAt(Date())
            .subject(user.loginId)
            .id(UUID.randomUUID().toString())
            .expiration(Date(Clock.System.now().toEpochMilliseconds() + expire))
            .signWith(getSigningKey(secret))
            .compact()
    }

    private fun getSigningKey(secret: String): SecretKey {
        val keyBytes = Decoders.BASE64.decode(secret)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    fun getClaimsFromToken(token: String): Claims{
        try{
            return Jwts.parser().verifyWith(getSigningKey(secret))
                .build()
                .parseSignedClaims(token)
                .payload
        }catch (e : ExpiredJwtException){
            throw ServiceException(ErrorCode.TOKEN_EXPIRED)
        }catch (e: Exception){
            throw ServiceException(ErrorCode.INVALID_TOKEN)
        }
    }
}