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
                val assignment = mockk<Assignment> {
                    every { title } returns "오늘 과제"
                    every { status } returns AssignmentStatus.SUBMITTED
                    every { startTime } returns testDate.plusHours(2) // 12:00
                    every { endTime } returns testDate.plusHours(3)
                }
                val todo = mockk<ToDo> {
                    every { title } returns "오늘 할 일"
                    every { status } returns Status.COMPLETED
                    every { startTime } returns testDate.plusHours(1) // 11:00 (Earlier)
                    every { endTime } returns testDate.plusHours(2)
                }
                val yesterdayTodo = mockk<ToDo> {
                    every { title } returns "어제 할 일"
                    every { status } returns Status.COMPLETED
                    every { startTime } returns testDate.minusDays(1)
                    every { endTime } returns testDate.minusDays(1).plusHours(1)
                }

                every { assignmentRepository.findAllByMentee_Id(menteeId) } returns listOf(assignment)
                every { toDoRepository.findAllByMentee_IdAndStatus(menteeId, Status.COMPLETED) } returns listOf(todo, yesterdayTodo)

                val result = menteeQueryUseCase.getTimeTable(menteeId, todayDate)

                result shouldHaveSize 2
                result[0].name shouldBe "오늘 할 일"
                result[1].name shouldBe "오늘 과제"
            }
        }
    }
})
