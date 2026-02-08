package com.barostartbe.domain.badge.usecase

import com.barostartbe.domain.assignment.usecase.AssignmentQueryUseCase
import com.barostartbe.domain.badge.entity.Badge
import com.barostartbe.domain.badge.entity.MenteeBadgeMapping
import com.barostartbe.domain.badge.repository.BadgeRepository
import com.barostartbe.domain.badge.repository.MenteeBadgeMappingRepository
import com.barostartbe.domain.comment.usecase.CommentQueryUseCase
import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.mentee.repository.MenteeRepository
import com.barostartbe.domain.todo.usecase.ToDoQueryUseCase
import com.barostartbe.domain.user.usecase.AccessLogQueryUseCase
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

class MenteeBadgeCommandUseCaseTest : DescribeSpec({
    val menteeBadgeMappingRepository = mockk<MenteeBadgeMappingRepository>(relaxed = true)
    val badgeRepository = mockk<BadgeRepository>(relaxed = true)
    val menteeRepository = mockk<MenteeRepository>(relaxed = true)
    val assignmentQueryUseCase = mockk<AssignmentQueryUseCase>(relaxed = true)
    val accessLogQueryUseCase = mockk<AccessLogQueryUseCase>(relaxed = true)
    val toDoQueryUseCase = mockk<ToDoQueryUseCase>(relaxed = true)
    val commentQueryUseCase = mockk<CommentQueryUseCase>(relaxed = true)

    val menteeBadgeCommandUseCase = MenteeBadgeCommandUseCase(
        menteeBadgeMappingRepository,
        badgeRepository,
        menteeRepository,
        assignmentQueryUseCase,
        accessLogQueryUseCase,
        toDoQueryUseCase,
        commentQueryUseCase
    )

    beforeEach {
        clearMocks(
            menteeBadgeMappingRepository,
            badgeRepository,
            menteeRepository,
            assignmentQueryUseCase,
            accessLogQueryUseCase,
            toDoQueryUseCase,
            commentQueryUseCase
        )
        every { menteeBadgeMappingRepository.save(any()) } answers { firstArg() }
    }

    describe("MenteeBadgeCommandUseCase") {
        val menteeId = 1L
        val mentee = mockk<Mentee>(relaxed = true)
        every { mentee.id } returns menteeId

        context("멘티 뱃지 업데이트 시") {
            it("멘티가 존재하지 않으면 예외를 던진다") {
                every { menteeRepository.findByIdOrNull(menteeId) } returns null

                val exception = shouldThrow<ServiceException> {
                    menteeBadgeCommandUseCase.updateBadgeForMentee(menteeId)
                }
                exception.errorCode shouldBe ErrorCode.MENTEE_NOT_FOUND
            }
        }

        context("뱃지 획득 조건 검증") {
            beforeEach {
                every { menteeRepository.findByIdOrNull(menteeId) } returns mentee
                every { menteeBadgeMappingRepository.findAllByMentee_id(menteeId) } returns emptyList()
            }

            it("'첫 과제 완료' 조건을 만족하면 뱃지를 저장한다") {
                val badge = mockk<Badge> {
                    every { id } returns 1L
                    every { name } returns "첫 과제 완료"
                }
                every { badgeRepository.findAll() } returns listOf(badge)
                every { assignmentQueryUseCase.checkCompletedAssignmentExists(menteeId) } returns true

                menteeBadgeCommandUseCase.updateBadgeForMentee(menteeId)

                val slot = slot<MenteeBadgeMapping>()
                verify(exactly = 1) { menteeBadgeMappingRepository.save(capture(slot)) }
                slot.captured.badge.name shouldBe "첫 과제 완료"
            }

            it("'7일 연속 출석' 조건을 만족하면 뱃지를 저장한다") {
                val badge = mockk<Badge> {
                    every { id } returns 2L
                    every { name } returns "7일 연속 출석"
                }
                every { badgeRepository.findAll() } returns listOf(badge)
                every { accessLogQueryUseCase.getCurrentConsecutiveDays(menteeId) } returns 7

                menteeBadgeCommandUseCase.updateBadgeForMentee(menteeId)

                val slot = slot<MenteeBadgeMapping>()
                verify(exactly = 1) { menteeBadgeMappingRepository.save(capture(slot)) }
                slot.captured.badge.name shouldBe "7일 연속 출석"
            }

            it("'30일 연속 출석' 조건을 만족하면 뱃지를 저장한다") {
                val badge = mockk<Badge> {
                    every { id } returns 3L
                    every { name } returns "30일 연속 출석"
                }
                every { badgeRepository.findAll() } returns listOf(badge)
                every { accessLogQueryUseCase.getCurrentConsecutiveDays(menteeId) } returns 30

                menteeBadgeCommandUseCase.updateBadgeForMentee(menteeId)

                val slot = slot<MenteeBadgeMapping>()
                verify(exactly = 1) { menteeBadgeMappingRepository.save(capture(slot)) }
                slot.captured.badge.name shouldBe "30일 연속 출석"
            }

            it("'주간목표 달성' 조건을 만족하면 뱃지를 저장한다") {
                val badge = mockk<Badge> {
                    every { id } returns 4L
                    every { name } returns "주간목표 달성"
                }
                every { badgeRepository.findAll() } returns listOf(badge)
                every { assignmentQueryUseCase.is7DaysAssignmentCompletedStreak(menteeId) } returns true

                menteeBadgeCommandUseCase.updateBadgeForMentee(menteeId)

                val slot = slot<MenteeBadgeMapping>()
                verify(exactly = 1) { menteeBadgeMappingRepository.save(capture(slot)) }
                slot.captured.badge.name shouldBe "주간목표 달성"
            }

            it("'오늘도 한 걸음' 조건을 만족하면 뱃지를 저장한다") {
                val badge = mockk<Badge> {
                    every { id } returns 5L
                    every { name } returns "오늘도 한 걸음"
                }
                every { badgeRepository.findAll() } returns listOf(badge)
                every { toDoQueryUseCase.is7DaysToDoCompletedStreak(menteeId) } returns true

                menteeBadgeCommandUseCase.updateBadgeForMentee(menteeId)

                val slot = slot<MenteeBadgeMapping>()
                verify(exactly = 1) { menteeBadgeMappingRepository.save(capture(slot)) }
                slot.captured.badge.name shouldBe "오늘도 한 걸음"
            }

            it("'질문왕' 조건을 만족하면 뱃지를 저장한다") {
                val badge = mockk<Badge> {
                    every { id } returns 6L
                    every { name } returns "질문왕"
                }
                every { badgeRepository.findAll() } returns listOf(badge)
                every { commentQueryUseCase.hasMoreThanTenComments(menteeId) } returns true

                menteeBadgeCommandUseCase.updateBadgeForMentee(menteeId)

                val slot = slot<MenteeBadgeMapping>()
                verify(exactly = 1) { menteeBadgeMappingRepository.save(capture(slot)) }
                slot.captured.badge.name shouldBe "질문왕"
            }

            it("'포모도로 마스터' 조건을 만족하면 뱃지를 저장한다") {
                val badge = mockk<Badge> {
                    every { id } returns 7L
                    every { name } returns "포모도로 마스터"
                }
                every { badgeRepository.findAll() } returns listOf(badge)
                every { toDoQueryUseCase.getCompletedOver25MinutesCount(menteeId) } returns 20

                menteeBadgeCommandUseCase.updateBadgeForMentee(menteeId)

                val slot = slot<MenteeBadgeMapping>()
                verify(exactly = 1) { menteeBadgeMappingRepository.save(capture(slot)) }
                slot.captured.badge.name shouldBe "포모도로 마스터"
            }

            it("'아침 루틴' 조건을 만족하면 뱃지를 저장한다") {
                val badge = mockk<Badge> {
                    every { id } returns 8L
                    every { name } returns "아침 루틴"
                }
                every { badgeRepository.findAll() } returns listOf(badge)
                every { toDoQueryUseCase.getStudyBetweenSixAndNineCount(menteeId) } returns 7

                menteeBadgeCommandUseCase.updateBadgeForMentee(menteeId)

                val slot = slot<MenteeBadgeMapping>()
                verify(exactly = 1) { menteeBadgeMappingRepository.save(capture(slot)) }
                slot.captured.badge.name shouldBe "아침 루틴"
            }
        }

        context("이미 획득한 뱃지인 경우") {
            it("조건을 만족해도 다시 저장하지 않는다") {
                val badge = mockk<Badge> {
                    every { id } returns 1L
                    every { name } returns "첫 과제 완료"
                }
                val existingMapping = mockk<MenteeBadgeMapping> {
                    every { this@mockk.badge } returns badge
                }

                every { menteeRepository.findByIdOrNull(menteeId) } returns mentee
                every { menteeBadgeMappingRepository.findAllByMentee_id(menteeId) } returns listOf(existingMapping)
                every { badgeRepository.findAll() } returns listOf(badge)
                every { assignmentQueryUseCase.checkCompletedAssignmentExists(menteeId) } returns true

                menteeBadgeCommandUseCase.updateBadgeForMentee(menteeId)

                verify(exactly = 0) { menteeBadgeMappingRepository.save(any()) }
            }
        }
    }
})
