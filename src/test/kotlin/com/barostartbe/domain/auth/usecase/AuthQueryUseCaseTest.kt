package com.barostartbe.domain.auth.usecase

import com.barostartbe.domain.auth.dto.LoginRequestDto
import com.barostartbe.domain.user.entity.Role
import com.barostartbe.domain.user.entity.User
import com.barostartbe.domain.user.repository.UserRepository
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import com.barostartbe.global.security.jwt.JwtUtil
import com.barostartbe.global.security.jwt.enum.TokenType
import io.jsonwebtoken.Claims
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.time.Duration

class AuthQueryUseCaseTest : BehaviorSpec({

    lateinit var userRepository: UserRepository
    lateinit var jwtUtil: JwtUtil
    lateinit var redisTemplate: RedisTemplate<String, Any>
    lateinit var valueOps: ValueOperations<String, Any>

    lateinit var authQueryUseCase: AuthQueryUseCase

    beforeTest {
        userRepository = mockk()
        jwtUtil = mockk()
        redisTemplate = mockk()
        valueOps = mockk()

        every { redisTemplate.opsForValue() } returns valueOps

        authQueryUseCase = AuthQueryUseCase(
            userRepository,
            jwtUtil,
            redisTemplate
        )
    }

    given("login 요청이 들어오면") {

        `when`("존재하는 유저라면") {
            then("토큰 페어를 반환하고 refreshToken을 redis에 저장한다") {
                val request = mockk<HttpServletRequest>()
                val loginRequestDto = createTestLoginRequest()
                val user = createTestUser()

                every { request.getAttribute("requestBody") } returns loginRequestDto
                every { userRepository.findUserByLoginId("testUser") } returns user
                every { jwtUtil.generateToken(TokenType.ACCESS, user) } returns "access-token"
                every { jwtUtil.generateToken(TokenType.REFRESH, user) } returns "refresh-token"
                every { valueOps.set(any(), any()) } just Runs

                val result = authQueryUseCase.login(request)

                result.accessToken shouldBe "access-token"
                result.refreshToken shouldBe "refresh-token"

                verify {
                    valueOps.set("redis::refresh::testUser", "refresh-token")
                }
            }
        }

        `when`("유저가 존재하지 않으면") {
            then("USER_NOT_FOUND 예외가 발생한다") {
                val request = mockk<HttpServletRequest>()
                val loginRequestDto = createTestLoginRequest()

                every { request.getAttribute("requestBody") } returns loginRequestDto
                every { userRepository.findUserByLoginId("testUser") } returns null

                shouldThrow<ServiceException> {
                    authQueryUseCase.login(request)
                }.errorCode shouldBe ErrorCode.USER_NOT_FOUND
            }
        }
    }

    given("refreshToken 재발급 요청 시") {

        `when`("redis에 저장된 refreshToken과 일치하면") {
            then("새 토큰 페어를 반환한다") {
                val refreshToken = "valid-refresh-token"
                val claims = mockk<Claims> {
                    every { subject } returns "testUser"
                }
                val user = createTestUser()

                every { jwtUtil.getClaimsFromToken(refreshToken) } returns claims
                every { valueOps.get("redis::refresh::testUser") } returns refreshToken
                every { userRepository.findUserByLoginId("testUser") } returns user
                every { jwtUtil.generateToken(any(), any()) } returnsMany listOf("new-access", "new-refresh")
                every { valueOps.set(any(), any()) } just Runs

                val result = authQueryUseCase.regenerateToken(refreshToken)

                result.accessToken shouldBe "new-access"
                result.refreshToken shouldBe "new-refresh"
            }
        }

        `when`("refreshToken이 일치하지 않으면") {
            then("INVALID_TOKEN 예외가 발생한다") {
                val refreshToken = "valid-refresh-token"
                val claims = mockk<Claims> {
                    every { subject } returns "testUser"
                }

                every { jwtUtil.getClaimsFromToken(refreshToken) } returns claims
                every { valueOps.get("redis::refresh::testUser") } returns "another-token"

                shouldThrow<ServiceException> {
                    authQueryUseCase.regenerateToken(refreshToken)
                }.errorCode shouldBe ErrorCode.INVALID_TOKEN
            }
        }
    }

    given("logout 요청 시") {

        `when`("정상 요청이면") {
            then("redis에서 토큰을 삭제하고 logout 정보를 저장한다") {
                val request = mockk<HttpServletRequest>()
                val claims = mockk<Claims> {
                    every { subject } returns "testUser"
                    every { id } returns "jwt-id"
                }

                every { request.getHeader("Authorization") } returns "Bearer access-token"
                every { jwtUtil.getClaimsFromToken("access-token") } returns claims
                every { redisTemplate.delete("testUser") } returns true
                every {
                    valueOps.set(
                        "redis::logout::jwt-id",
                        true,
                        Duration.ofMinutes(30)
                    )
                } just Runs

                authQueryUseCase.logout(request)

                verify {
                    redisTemplate.delete("testUser")
                    valueOps.set(
                        "redis::logout::jwt-id",
                        true,
                        Duration.ofMinutes(30)
                    )
                }
            }
        }
    }
})

private fun createTestLoginRequest() = LoginRequestDto(
    loginId = "testUser",
    password = "password"
)

private fun createTestUser() = User(
    loginId = "testUser",
    password = "password",
    name = "name",
    nickname = "nickname",
    role = Role.MENTEE
)
