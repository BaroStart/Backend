package com.barostartbe.domain.mentee.usecase

import com.barostartbe.domain.admin.repository.MentorMenteeMappingRepository
import com.barostartbe.domain.assignment.entity.Assignment
import com.barostartbe.domain.assignment.entity.enums.AssignmentStatus
import com.barostartbe.domain.assignment.repository.AssignmentRepository
import com.barostartbe.domain.mentee.repository.MenteeRepository
import com.barostartbe.domain.mentor.repository.MentorRepository
import com.barostartbe.domain.todo.entity.ToDo
import com.barostartbe.domain.todo.entity.enums.Status
import com.barostartbe.domain.todo.repository.ToDoRepository
import com.barostartbe.domain.user.repository.AccessLogRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.redis.core.RedisTemplate
import java.time.LocalDate
import java.time.LocalDateTime

class MenteeQueryUseCaseTest : DescribeSpec({
    val mentorRepository = mockk<MentorRepository>()
    val menteeRepository = mockk<MenteeRepository>()
    val accessLogRepository = mockk<AccessLogRepository>()
    val mentorMenteeMappingRepository = mockk<MentorMenteeMappingRepository>()
    val redisTemplate = mockk<RedisTemplate<String, Any>>()
    val assignmentRepository = mockk<AssignmentRepository>()
    val toDoRepository = mockk<ToDoRepository>()

    val menteeQueryUseCase = MenteeQueryUseCase(
        mentorRepository,
        menteeRepository,
        accessLogRepository,
        mentorMenteeMappingRepository,
        redisTemplate,
        assignmentRepository,
        toDoRepository
    )

    describe("MenteeQueryUseCase") {
        context("getTimeTable") {
            val menteeId = 1L
            val testDate = LocalDateTime.of(2023, 10, 1, 10, 0)
            val todayDate = testDate.toLocalDate()

            it("오늘 수행한 과제와 할 일을 반환하고 시작 시간 순으로 정렬한다") {
                val assignment = mockk<Assignment>(relaxed = true) {
                    every { title } returns "오늘 과제"
                    every { status } returns AssignmentStatus.SUBMITTED
                    every { startTime } returns testDate.plusHours(2) // 12:00
                    every { endTime } returns testDate.plusHours(3)
                }
                val todo = mockk<ToDo>(relaxed = true) {
                    every { title } returns "오늘 할 일"
                    every { status } returns Status.COMPLETED
                    every { startTime } returns testDate.plusHours(1) // 11:00 (Earlier)
                    every { endTime } returns testDate.plusHours(2)
                }

                every { assignmentRepository.findAllByMenteeIdAndStartTimeDate(menteeId, todayDate) } returns listOf(assignment)
                every { toDoRepository.findAllCompletedByMenteeIdAndStartTimeDate(menteeId, todayDate) } returns listOf(todo)

                val result = menteeQueryUseCase.getTimeTable(menteeId, todayDate)

                result shouldHaveSize 2
                result[0].name shouldBe "오늘 할 일"
                result[1].name shouldBe "오늘 과제"
            }
        }

        context("getCalendar") {
            val menteeId = 1L
            val year = 2023
            val month = 10
            val startDate = LocalDate.of(year, month, 1)
            val endDate = startDate.withDayOfMonth(startDate.lengthOfMonth())

            it("특정 년월의 과제와 할 일 유무를 반환한다") {
                val assignment = mockk<Assignment>(relaxed = true) {
                    every { createdAt } returns LocalDateTime.of(2023, 10, 5, 0, 0)
                    every { dueDate } returns LocalDateTime.of(2023, 10, 7, 23, 59)
                }
                val todo = mockk<ToDo>(relaxed = true) {
                    every { createdAt } returns LocalDateTime.of(2023, 10, 10, 0, 0)
                }

                every { assignmentRepository.findAllByMenteeIdAndDateOverlapping(menteeId, startDate, endDate) } returns listOf(assignment)
                every { toDoRepository.findAllByMenteeIdAndCreatedAtBetween(menteeId, startDate, endDate) } returns listOf(todo)

                val result = menteeQueryUseCase.getCalendar(menteeId, year, month)

                result shouldHaveSize 31

                result.find { it.date == LocalDate.of(2023, 10, 5) }?.hasAssignment shouldBe true
                result.find { it.date == LocalDate.of(2023, 10, 6) }?.hasAssignment shouldBe true
                result.find { it.date == LocalDate.of(2023, 10, 7) }?.hasAssignment shouldBe true
                result.find { it.date == LocalDate.of(2023, 10, 8) }?.hasAssignment shouldBe false
                

                result.find { it.date == LocalDate.of(2023, 10, 10) }?.hasToDo shouldBe true
                result.find { it.date == LocalDate.of(2023, 10, 11) }?.hasToDo shouldBe false
                
                result.find { it.date == LocalDate.of(2023, 10, 1) }?.hasAssignment shouldBe false
                result.find { it.date == LocalDate.of(2023, 10, 1) }?.hasToDo shouldBe false
            }
        }
    }
})
