package com.barostartbe.domain.auth.usecase

import com.barostartbe.domain.auth.dto.SignupRequestDto
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class AuthCommandUseCaseTest : DescribeSpec({
    val userRepository = mockk<UserRepository>(relaxed = true)
    val authCommandUseCase = AuthCommandUseCase(userRepository, BCryptPasswordEncoder())

    beforeEach { clearMocks(userRepository) }

    describe("AuthCommandUseCase") {
        context("회원가입을 진행할 때"){
            val loginId = "loginId"
            val request = SignupRequestDto(
                loginId = loginId,
                password = "password",
                name = "name",
                nickname = "nickname"
            )

            it("이미 회원가입을 했을 경우 오류를 발생시킨다."){
                every { userRepository.existsUserByLoginId(loginId)} returns true

                shouldThrow<ServiceException> {
                    authCommandUseCase.createUser(request)
                }.errorCode shouldBe ErrorCode.DUPLICATED_LOGIN_ID

                verify(exactly = 1) { userRepository.existsUserByLoginId(loginId) }
            }
        }

    }
})