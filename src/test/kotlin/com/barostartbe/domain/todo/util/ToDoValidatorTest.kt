package com.barostartbe.domain.todo.util

import com.barostartbe.domain.assignment.repository.AssignmentRepository
import com.barostartbe.domain.todo.repository.ToDoRepository
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime

class ToDoValidatorTest : DescribeSpec({
    val toDoRepository = mockk<ToDoRepository>()
    val assignmentRepository = mockk<AssignmentRepository>()
    val toDoValidator = ToDoValidator(toDoRepository, assignmentRepository)

    describe("ToDoValidator") {
        context("validateTimeConflict") {
            val menteeId = 1L
            val startTime = LocalDateTime.of(2023, 1, 1, 10, 0)
            val endTime = LocalDateTime.of(2023, 1, 1, 11, 0)

            it("과제와 시간이 겹치면 예외를 발생시킨다") {
                every { assignmentRepository.existsByMenteeIdAndTimeRange(menteeId, startTime, endTime) } returns true

                val exception = shouldThrow<ServiceException> {
                    toDoValidator.validateTimeConflict(menteeId, startTime, endTime)
                }
                exception.errorCode shouldBe ErrorCode.TODO_TIME_CONFLICT_WITH_ASSIGNMENT
            }

            it("다른 할 일과 시간이 겹치면 예외를 발생시킨다") {
                every { assignmentRepository.existsByMenteeIdAndTimeRange(menteeId, startTime, endTime) } returns false
                every { toDoRepository.existsByMenteeIdAndTimeRange(menteeId, startTime, endTime) } returns true

                val exception = shouldThrow<ServiceException> {
                    toDoValidator.validateTimeConflict(menteeId, startTime, endTime)
                }
                exception.errorCode shouldBe ErrorCode.TODO_TIME_CONFLICT_WITH_TODO
            }

            it("겹치는 시간이 없으면 통과한다") {
                every { assignmentRepository.existsByMenteeIdAndTimeRange(menteeId, startTime, endTime) } returns false
                every { toDoRepository.existsByMenteeIdAndTimeRange(menteeId, startTime, endTime) } returns false

                toDoValidator.validateTimeConflict(menteeId, startTime, endTime)
            }
        }
    }
})
