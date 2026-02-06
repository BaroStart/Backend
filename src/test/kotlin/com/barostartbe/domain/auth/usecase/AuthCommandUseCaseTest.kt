package com.barostartbe.domain.auth.usecase

import com.barostartbe.domain.auth.dto.LoginRequestDto
import com.barostartbe.domain.auth.dto.SignupRequestDto
import com.barostartbe.domain.auth.dto.TokenPairResponseDto
import com.barostartbe.domain.user.entity.User
import com.barostartbe.domain.user.repository.AccessLogRepository
import com.barostartbe.domain.user.repository.UserRepository
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class AuthCommandUseCaseTest : DescribeSpec({
    val authQueryUseCase = mockk<AuthQueryUseCase>(relaxed = true)
    val userRepository = mockk<UserRepository>(relaxed = true)
    val accessLogRepository = mockk<AccessLogRepository>(relaxed = true)
    val authCommandUseCase = AuthCommandUseCase(
        authQueryUseCase,
        userRepository,
        accessLogRepository,
        BCryptPasswordEncoder()
    )

    beforeEach { clearMocks(authQueryUseCase, userRepository, accessLogRepository) }

    describe("AuthCommandUseCase") {
        context("회원가입을 진행할 때"){
            val loginId = "loginId"
            val request = SignupRequestDto(
                loginId = loginId,
                password = "password",
                name = "name",
                nickname = "nickname",
                joinType = "MENTEE",
                grade = "FIRST",
                school = "NORMAL",
                hopeMajor = "medical",
                university = null
            )

            it("이미 회원가입을 했을 경우 오류를 발생시킨다."){
                every { userRepository.existsUserByLoginId(loginId)} returns true

                shouldThrow<ServiceException> {
                    authCommandUseCase.createUser(request)
                }.errorCode shouldBe ErrorCode.DUPLICATED_LOGIN_ID

                verify(exactly = 1) { userRepository.existsUserByLoginId(loginId) }
            }
        }

        context("로그인을 진행할 때") {
            val loginId = "loginId"
            val loginRequestDto = LoginRequestDto(loginId = loginId, password = "password")

            it("정상적으로 로그인하면 토큰을 반환한다.") {
                val servletRequest = mockk<HttpServletRequest>()
                val user = mockk<User>()
                val tokenPair = TokenPairResponseDto("access", "refresh")
                val userId = 1L

                every { servletRequest.getAttribute("requestBody") } returns loginRequestDto
                every { userRepository.findUserByLoginId(loginId) } returns user
                every { user.id } returns userId
                every { user.loginId } returns loginId
                every { accessLogRepository.save(any()) } returns mockk()
                every { authQueryUseCase.generateTokenPairAndSaveRefreshTokenInRedis(loginId) } returns tokenPair

                val result = authCommandUseCase.login(servletRequest)

                result shouldBe tokenPair
                verify(exactly = 1) { userRepository.findUserByLoginId(loginId) }
                verify(exactly = 1) {
                    accessLogRepository.save(withArg { it.userId shouldBe userId })
                }
                verify(exactly = 1) { authQueryUseCase.generateTokenPairAndSaveRefreshTokenInRedis(loginId) }
            }

            it("존재하지 않는 회원일 경우 오류를 발생시킨다.") {
                val servletRequest = mockk<HttpServletRequest>()

                every { servletRequest.getAttribute("requestBody") } returns loginRequestDto
                every { userRepository.findUserByLoginId(loginId) } returns null

                shouldThrow<ServiceException> {
                    authCommandUseCase.login(servletRequest)
                }.errorCode shouldBe ErrorCode.USER_NOT_FOUND

                verify(exactly = 1) { userRepository.findUserByLoginId(loginId) }
                verify(exactly = 0) { accessLogRepository.save(any()) }
                verify(exactly = 0) { authQueryUseCase.generateTokenPairAndSaveRefreshTokenInRedis(any()) }
            }
        }

        context("접근 로그를 저장할 때") {
            it("AccessLogRepository에 저장 요청을 전달한다.") {
                val userId = 2L
                every { accessLogRepository.save(any()) } returns mockk()

                authCommandUseCase.saveAccessLog(userId)

                verify(exactly = 1) {
                    accessLogRepository.save(withArg { it.userId shouldBe userId })
                }
            }
        }
    }
})
