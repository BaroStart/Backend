package com.barostartbe.domain.overall.usecase

import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.mentee.repository.MenteeRepository
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.domain.mentor.repository.MentorRepository
import com.barostartbe.domain.notification.dto.request.SendNotificationRequest
import com.barostartbe.domain.notification.usecase.SendNotificationUseCase
import com.barostartbe.domain.overall.dto.request.OverallCreateRequest
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
import io.mockk.slot
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull

class OverallCommandUseCaseTest : DescribeSpec({
    val overallRepository = mockk<OverallRepository>(relaxed = true)
    val menteeRepository = mockk<MenteeRepository>(relaxed = true)
    val mentorRepository = mockk<MentorRepository>(relaxed = true)
    val sendNotificationUseCase = mockk<SendNotificationUseCase>(relaxed = true)

    val overallCommandUseCase = OverallCommandUseCase(
        overallRepository,
        menteeRepository,
        mentorRepository,
        sendNotificationUseCase
    )

    beforeEach {
        clearMocks(overallRepository, menteeRepository, mentorRepository, sendNotificationUseCase)
    }

    describe("OverallCommandUseCase") {
        val mentorId = 1L
        val menteeId = 2L
        val overallCreateRequest = OverallCreateRequest(menteeId, "훌륭한 멘티입니다.")

        val mentor = mockk<Mentor>(relaxed = true) {
            every { id } returns mentorId
            every { name } returns "김멘토"
        }
        val mentee = mockk<Mentee>(relaxed = true) {
            every { id } returns menteeId
        }

        context("총평 생성 시") {
            it("멘토와 멘티가 존재하면 총평을 저장하고 알림을 전송한다") {
                every { mentorRepository.findByIdOrNull(mentorId) } returns mentor
                every { menteeRepository.findByIdOrNull(menteeId) } returns mentee
                every { overallRepository.save(any()) } returns mockk<Overall>()

                overallCommandUseCase.create(overallCreateRequest, mentorId)

                val overallSlot = slot<Overall>()
                verify(exactly = 1) { overallRepository.save(capture(overallSlot)) }
                overallSlot.captured.content shouldBe "훌륭한 멘티입니다."
                overallSlot.captured.mentee shouldBe mentee
                overallSlot.captured.mentor shouldBe mentor

                val notificationSlot = slot<SendNotificationRequest>()
                verify(exactly = 1) { sendNotificationUseCase.execute(capture(notificationSlot)) }
                notificationSlot.captured.receiverId shouldBe menteeId
            }

            it("멘토가 존재하지 않으면 예외를 던진다") {
                every { mentorRepository.findByIdOrNull(mentorId) } returns null

                val exception = shouldThrow<ServiceException> {
                    overallCommandUseCase.create(overallCreateRequest, mentorId)
                }
                exception.errorCode shouldBe ErrorCode.MENTOR_NOT_FOUND
            }

            it("멘티가 존재하지 않으면 예외를 던진다") {
                every { mentorRepository.findByIdOrNull(mentorId) } returns mentor
                every { menteeRepository.findByIdOrNull(menteeId) } returns null

                val exception = shouldThrow<ServiceException> {
                    overallCommandUseCase.create(overallCreateRequest, mentorId)
                }
                exception.errorCode shouldBe ErrorCode.MENTEE_NOT_FOUND
            }
        }
    }
})
