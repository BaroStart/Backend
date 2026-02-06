package com.barostartbe.domain.todo.usecase

import com.barostartbe.domain.mentee.entity.Grade
import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.mentee.entity.School
import com.barostartbe.domain.todo.entity.ToDo
import com.barostartbe.domain.todo.entity.enums.Status
import com.barostartbe.domain.todo.error.ToDoNotFoundException
import com.barostartbe.domain.todo.repository.ToDoRepository
import com.barostartbe.domain.todo.repository.ToDoTimeRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.*
import org.springframework.data.repository.findByIdOrNull

class DeleteToDoUseCaseTest : DescribeSpec({

    val toDoRepository = mockk<ToDoRepository>(relaxed = true)
    val toDoTimeRepository = mockk<ToDoTimeRepository>(relaxed = true)
    val deleteToDoUseCase = DeleteToDoUseCase(toDoRepository, toDoTimeRepository)

    beforeEach {
        clearMocks(toDoRepository, toDoTimeRepository)
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

            it("할 일이 존재하면 연관된 시간 정보와 함께 성공적으로 삭제된다") {
                every { toDoRepository.findByIdOrNull(todoId) } returns toDo
                every { toDoTimeRepository.deleteAllByToDo_Id(todoId) } just Runs
                every { toDoRepository.delete(toDo) } just Runs

                deleteToDoUseCase.execute(todoId)

                verify(exactly = 1) { toDoRepository.findByIdOrNull(todoId) }
                verify(exactly = 1) { toDoTimeRepository.deleteAllByToDo_Id(todoId) }
                verify(exactly = 1) { toDoRepository.delete(toDo) }
            }

            it("시간 정보가 먼저 삭제된 후 할 일이 삭제된다") {
                val deletionOrder = mutableListOf<String>()

                every { toDoRepository.findByIdOrNull(todoId) } returns toDo
                every { toDoTimeRepository.deleteAllByToDo_Id(todoId) } answers {
                    deletionOrder.add("ToDoTime")
                }
                every { toDoRepository.delete(toDo) } answers {
                    deletionOrder.add("ToDo")
                }

                deleteToDoUseCase.execute(todoId)

                verifyOrder {
                    toDoTimeRepository.deleteAllByToDo_Id(todoId)
                    toDoRepository.delete(toDo)
                }
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
                verify(exactly = 0) { toDoTimeRepository.deleteAllByToDo_Id(any()) }
                verify(exactly = 0) { toDoRepository.delete(any()) }
            }
        }
    }
})
