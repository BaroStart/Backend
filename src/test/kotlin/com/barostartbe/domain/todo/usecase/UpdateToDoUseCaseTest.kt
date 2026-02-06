package com.barostartbe.domain.todo.usecase

import com.barostartbe.domain.mentee.entity.Grade
import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.mentee.entity.School
import com.barostartbe.domain.todo.dto.base.TimeSlot
import com.barostartbe.domain.todo.dto.request.UpdateToDoReq
import com.barostartbe.domain.todo.entity.ToDo
import com.barostartbe.domain.todo.entity.ToDoTime
import com.barostartbe.domain.todo.entity.enums.Status
import com.barostartbe.domain.todo.error.ToDoNotFoundException
import com.barostartbe.domain.todo.repository.ToDoRepository
import com.barostartbe.domain.todo.repository.ToDoTimeRepository
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

class UpdateToDoUseCaseTest : DescribeSpec({

    val toDoRepository = mockk<ToDoRepository>(relaxed = true)
    val toDoTimeRepository = mockk<ToDoTimeRepository>(relaxed = true)
    val updateToDoUseCase = UpdateToDoUseCase(toDoRepository, toDoTimeRepository)

    beforeEach {
        clearMocks(toDoRepository, toDoTimeRepository)
    }

    describe("UpdateToDoUseCase") {
        context("완료된 할 일을 수정할 때") {
            val todoId = 1L
            val updatedTitle = "영어 문제 풀기"
            val mentee = Mentee(
                loginId = "testId",
                password = "password123",
                name = "홍길동",
                nickname = "길동이",
                grade = Grade.FIRST,
                school = School.NORMAL,
                hopeMajor = "컴퓨터공학"
            )
            val completedToDo = mockk<ToDo>(relaxed = true) {
                every { id } returns todoId
                every { title } returns "국어 문제 풀기"
                every { status } returns Status.COMPLETED
                every { this@mockk.mentee } returns mentee
            }
            val timeSlot = TimeSlot(
                startTime = LocalDateTime.of(2026, 2, 5, 10, 0),
                endTime = LocalDateTime.of(2026, 2, 5, 11, 0)
            )
            val updateToDoReq = UpdateToDoReq(
                id = todoId,
                title = updatedTitle,
                timeList = listOf(timeSlot)
            )

            it("할 일이 완료 상태이고 시간 목록이 있으면 성공적으로 수정된다") {
                every { toDoRepository.findByIdOrNull(todoId) } returns completedToDo
                every { toDoTimeRepository.deleteAllByToDo_Id(todoId) } just Runs
                every { toDoTimeRepository.saveAll(any<List<ToDoTime>>()) } returns listOf()
                every { toDoRepository.save(any()) } returns completedToDo

                updateToDoUseCase.execute(updateToDoReq)

                verify(exactly = 1) { toDoRepository.findByIdOrNull(todoId) }
                verify(exactly = 1) { toDoTimeRepository.deleteAllByToDo_Id(todoId) }
                verify(exactly = 1) { toDoTimeRepository.saveAll(any<List<ToDoTime>>()) }
                verify(exactly = 1) { toDoRepository.save(any()) }
            }

            it("시간 목록이 null이면 ToDoTime을 생성하지 않는다") {
                val updateReqWithoutTime = UpdateToDoReq(
                    id = todoId,
                    title = updatedTitle,
                    timeList = null
                )
                every { toDoRepository.findByIdOrNull(todoId) } returns completedToDo
                every { toDoTimeRepository.deleteAllByToDo_Id(todoId) } just Runs
                every { toDoRepository.save(any()) } returns completedToDo

                updateToDoUseCase.execute(updateReqWithoutTime)

                verify(exactly = 1) { toDoRepository.findByIdOrNull(todoId) }
                verify(exactly = 1) { toDoTimeRepository.deleteAllByToDo_Id(todoId) }
                verify(exactly = 0) { toDoTimeRepository.saveAll(any<List<ToDoTime>>()) }
                verify(exactly = 1) { toDoRepository.save(any()) }
            }
        }

        context("미완료된 할 일을 수정할 때") {
            val todoId = 1L
            val updatedTitle = "영어 문제 풀기"
            val mentee = Mentee(
                loginId = "testId",
                password = "password123",
                name = "홍길동",
                nickname = "길동이",
                grade = Grade.FIRST,
                school = School.NORMAL,
                hopeMajor = "컴퓨터공학"
            )
            val notCompletedToDo = mockk<ToDo>(relaxed = true) {
                every { id } returns todoId
                every { title } returns "국어 문제 풀기"
                every { status } returns Status.NOT_COMPLETED
                every { this@mockk.mentee } returns mentee
            }
            val updateToDoReq = UpdateToDoReq(
                id = todoId,
                title = updatedTitle,
                timeList = null
            )

            it("할 일이 완료되지 않았으면 ServiceException을 던진다") {
                every { toDoRepository.findByIdOrNull(todoId) } returns notCompletedToDo

                val exception = shouldThrow<ServiceException> {
                    updateToDoUseCase.execute(updateToDoReq)
                }

                exception.errorCode shouldBe ErrorCode.TODO_NOT_COMPLETED
                verify(exactly = 1) { toDoRepository.findByIdOrNull(todoId) }
                verify(exactly = 0) { toDoTimeRepository.deleteAllByToDo_Id(any()) }
                verify(exactly = 0) { toDoRepository.save(any()) }
            }
        }

        context("존재하지 않는 할 일을 수정할 때") {
            val nonExistentId = 999L
            val updateToDoReq = UpdateToDoReq(
                id = nonExistentId,
                title = "테스트",
                timeList = null
            )

            it("할 일을 찾을 수 없으면 ToDoNotFoundException을 던진다") {
                every { toDoRepository.findByIdOrNull(nonExistentId) } returns null

                shouldThrow<ToDoNotFoundException> {
                    updateToDoUseCase.execute(updateToDoReq)
                }

                verify(exactly = 1) { toDoRepository.findByIdOrNull(nonExistentId) }
                verify(exactly = 0) { toDoRepository.save(any()) }
            }
        }
    }
})
