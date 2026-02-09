package com.barostartbe.domain.overall.usecase

import com.barostartbe.domain.overall.entity.Overall
import com.barostartbe.domain.overall.repository.OverallRepository
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate

class OverallQueryUseCaseTest : DescribeSpec({
    val overallRepository = mockk<OverallRepository>(relaxed = true)

    val overallQueryUseCase = OverallQueryUseCase(overallRepository)

    beforeEach {
        clearMocks(overallRepository)
    }

    describe("OverallQueryUseCase") {
        val menteeId = 1L
        val mentorId = 2L
        val date = LocalDate.now()

        context("멘티가 총평 조회 시") {
            it("해당 날짜에 받은 모든 총평 목록을 반환한다") {
                val overall = mockk<Overall>(relaxed = true) {
                    every { id } returns 1L
                    every { content } returns "멘토 A의 총평"
                    every { mentor.name } returns "멘토A"
                    every { createdAt } returns date.atStartOfDay()
                }
                every { overallRepository.findAllByMenteeIdAndDate(menteeId, date) } returns listOf(overall)

                val result = overallQueryUseCase.getForMentee(menteeId, date)

                result.size shouldBe 1
                result[0].content shouldBe "멘토 A의 총평"
            }

            it("총평이 없으면 예외를 던진다") {
                every { overallRepository.findAllByMenteeIdAndDate(menteeId, date) } returns emptyList()

                val exception = shouldThrow<ServiceException> {
                    overallQueryUseCase.getForMentee(menteeId, date)
                }
                exception.errorCode shouldBe ErrorCode.OVERALL_NOT_FOUND
            }
        }

        context("멘토가 총평 조회 시") {
            it("자신이 작성한 총평을 반환한다") {
                val overall = mockk<Overall>(relaxed = true) {
                    every { id } returns 1L
                    every { content } returns "내가 쓴 총평"
                    every { mentor.name } returns "나"
                    every { createdAt } returns date.atStartOfDay()
                }
                every { overallRepository.findByMenteeIdAndMentorIdAndDate(menteeId, mentorId, date) } returns overall

                val result = overallQueryUseCase.getForMentor(menteeId, mentorId, date)

                result.content shouldBe "내가 쓴 총평"
            }

            it("작성한 총평이 없으면 예외를 던진다") {
                every { overallRepository.findByMenteeIdAndMentorIdAndDate(menteeId, mentorId, date) } returns null

                val exception = shouldThrow<ServiceException> {
                    overallQueryUseCase.getForMentor(menteeId, mentorId, date)
                }
                exception.errorCode shouldBe ErrorCode.OVERALL_NOT_FOUND
            }
        }
    }
})