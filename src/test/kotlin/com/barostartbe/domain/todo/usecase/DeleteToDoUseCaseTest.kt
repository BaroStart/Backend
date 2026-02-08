package com.barostartbe.domain.todo.usecase

import com.barostartbe.domain.mentee.entity.Grade
import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.mentee.entity.School
import com.barostartbe.domain.todo.entity.ToDo
import com.barostartbe.domain.todo.entity.enums.Status
import com.barostartbe.domain.todo.error.ToDoNotFoundException
import com.barostartbe.domain.todo.repository.ToDoRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.*
import org.springframework.data.repository.findByIdOrNull

class DeleteToDoUseCaseTest : DescribeSpec({

    val toDoRepository = mockk<ToDoRepository>(relaxed = true)
    val deleteToDoUseCase = DeleteToDoUseCase(toDoRepository)

    beforeEach {
        clearMocks(toDoRepository)
    }

    describe("DeleteToDoUseCase") {
        context("할 일을 삭제할 때") {
            val todoId = 1L
            val mentee = Mentee(
                loginId = "testId",
                password = "password123",
                name = "홍길동",
                nickname = "길동이",
                grade = Grade.FIRST,
                school = School.NORMAL,
                hopeMajor = "컴퓨터공학"
            )
            val toDo = mockk<ToDo>(relaxed = true) {
                every { id } returns todoId
                every { title } returns "국어 문제 풀기"
                every { status } returns Status.NOT_COMPLETED
                every { this@mockk.mentee } returns mentee
            }

            it("할 일이 존재하면 성공적으로 삭제된다") {
                every { toDoRepository.findByIdOrNull(todoId) } returns toDo
                every { toDoRepository.delete(toDo) } just Runs

                deleteToDoUseCase.execute(todoId)

                verify(exactly = 1) { toDoRepository.findByIdOrNull(todoId) }
                verify(exactly = 1) { toDoRepository.delete(toDo) }
            }
        }

        context("존재하지 않는 할 일을 삭제할 때") {
            val nonExistentId = 999L

            it("할 일을 찾을 수 없으면 ToDoNotFoundException을 던진다") {
                every { toDoRepository.findByIdOrNull(nonExistentId) } returns null

                shouldThrow<ToDoNotFoundException> {
                    deleteToDoUseCase.execute(nonExistentId)
                }

                verify(exactly = 1) { toDoRepository.findByIdOrNull(nonExistentId) }
                verify(exactly = 0) { toDoRepository.delete(any()) }
            }
        }
    }
})
