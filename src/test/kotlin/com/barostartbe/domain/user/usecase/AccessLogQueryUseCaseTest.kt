package com.barostartbe.domain.user.usecase

import com.barostartbe.domain.user.repository.AccessLogRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk

class AccessLogQueryUseCaseTest : DescribeSpec({
    val accessLogRepository = mockk<AccessLogRepository>(relaxed = true)
    val accessLogQueryUseCase = AccessLogQueryUseCase(accessLogRepository)

    beforeEach {
        clearMocks(accessLogRepository)
    }

    describe("AccessLogQueryUseCase") {
        val menteeId = 1L

        it("현재 연속 출석 일수를 반환한다") {
            every { accessLogRepository.findCurrentConsecutiveDays(menteeId) } returns 10
            accessLogQueryUseCase.getCurrentConsecutiveDays(menteeId) shouldBe 10
        }
    }
})
