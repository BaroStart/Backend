package com.barostartbe.domain.todo.usecase

import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.todo.dto.request.UpdateToDoStatusReq
import com.barostartbe.domain.todo.entity.ToDo
import com.barostartbe.domain.todo.entity.enums.Status
import com.barostartbe.domain.todo.error.ToDoNotFoundException
import com.barostartbe.domain.todo.repository.ToDoRepository
import com.barostartbe.domain.todo.util.ToDoValidator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

class ChangeToDoStatusUseCaseTest : DescribeSpec({

    val toDoRepository = mockk<ToDoRepository>(relaxed = true)
    val toDoValidator = mockk<ToDoValidator>(relaxed = true)
    val changeToDoStatusUseCase = ChangeToDoStatusUseCase(toDoRepository, toDoValidator)

    beforeEach {
        clearMocks(toDoRepository, toDoValidator)
    }

    describe("ChangeToDoStatusUseCase") {
        context("할 일의 상태를 변경할 때") {
            val todoId = 1L
            val user = mockk<Mentee>(relaxed = true) {
                every { id } returns 1L
            }
            val toDo = mockk<ToDo>(relaxed = true) {
                every { id } returns todoId
                every { title } returns "국어 문제 풀기"
                every { status } returns Status.NOT_COMPLETED
                every { this@mockk.mentee } returns user
            }
            val updateToDoStatusReq = UpdateToDoStatusReq(
                id = todoId,
                status = Status.COMPLETED,
                startTime = LocalDateTime.of(2026, 2, 5, 10, 0),
                endTime = LocalDateTime.of(2026, 2, 5, 11, 0)
            )

            it("상태와 함께 시간 정보가 업데이트된다") {
                every { toDoRepository.findByIdOrNull(todoId) } returns toDo
                every { toDoRepository.save(any()) } returns toDo
                every { toDoValidator.validateTimeConflict(any(), any(), any()) } just Runs

                changeToDoStatusUseCase.execute(updateToDoStatusReq, user)

                verify(exactly = 1) { toDoValidator.validateTimeConflict(user.id!!, updateToDoStatusReq.startTime!!, updateToDoStatusReq.endTime!!) }
                verify(exactly = 1) { toDoRepository.findByIdOrNull(todoId) }
                verify(exactly = 1) { toDo.updateStatus(updateToDoStatusReq) }
                verify(exactly = 1) { toDoRepository.save(any()) }
            }
        }

        context("존재하지 않는 할 일의 상태를 변경할 때") {
            val nonExistentId = 999L
            val user = mockk<Mentee>(relaxed = true) {
                every { id } returns 1L
            }
            val updateToDoStatusReq = UpdateToDoStatusReq(
                id = nonExistentId,
                status = Status.COMPLETED,
                startTime = LocalDateTime.now(),
                endTime = LocalDateTime.now().plusHours(1)
            )

            it("할 일을 찾을 수 없으면 ToDoNotFoundException을 던진다") {
                every { toDoRepository.findByIdOrNull(nonExistentId) } returns null
                every { toDoValidator.validateTimeConflict(any(), any(), any()) } just Runs

                shouldThrow<ToDoNotFoundException> {
                    changeToDoStatusUseCase.execute(updateToDoStatusReq, user)
                }

                verify(exactly = 1) { toDoValidator.validateTimeConflict(any(), any(), any()) }
                verify(exactly = 1) { toDoRepository.findByIdOrNull(nonExistentId) }
                verify(exactly = 0) { toDoRepository.save(any()) }
            }
        }
    }
})
