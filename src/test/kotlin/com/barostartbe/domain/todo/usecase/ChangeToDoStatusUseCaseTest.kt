package com.barostartbe.domain.todo.usecase

import com.barostartbe.domain.mentee.entity.Grade
import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.mentee.entity.School
import com.barostartbe.domain.todo.dto.base.TimeSlot
import com.barostartbe.domain.todo.dto.request.UpdateToDoStatusReq
import com.barostartbe.domain.todo.entity.ToDo
import com.barostartbe.domain.todo.entity.ToDoTime
import com.barostartbe.domain.todo.entity.enums.Status
import com.barostartbe.domain.todo.error.ToDoNotFoundException
import com.barostartbe.domain.todo.repository.ToDoRepository
import com.barostartbe.domain.todo.repository.ToDoTimeRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.*
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

class ChangeToDoStatusUseCaseTest : DescribeSpec({

    val toDoRepository = mockk<ToDoRepository>(relaxed = true)
    val toDoTimeRepository = mockk<ToDoTimeRepository>(relaxed = true)
    val changeToDoStatusUseCase = ChangeToDoStatusUseCase(toDoRepository, toDoTimeRepository)

    beforeEach {
        clearMocks(toDoRepository, toDoTimeRepository)
    }

    describe("ChangeToDoStatusUseCase") {
        context("할 일의 상태를 변경할 때") {
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
            val toDoTime = ToDoTime(
                startTime = LocalDateTime.of(2026, 2, 5, 9, 0),
                endTime = LocalDateTime.of(2026, 2, 5, 10, 0),
                toDo = toDo
            )
            val timeSlot = TimeSlot(
                startTime = LocalDateTime.of(2026, 2, 5, 10, 0),
                endTime = LocalDateTime.of(2026, 2, 5, 11, 0)
            )
            val updateToDoStatusReq = UpdateToDoStatusReq(
                id = todoId,
                status = Status.COMPLETED,
                timeList = listOf(timeSlot)
            )

            it("시간 목록이 있으면 상태 변경과 함께 시간 정보가 업데이트된다") {
                every { toDoRepository.findByIdOrNull(todoId) } returns toDo
                every { toDoTimeRepository.deleteByToDo_Id(todoId) } just Runs
                every { toDoTimeRepository.saveAll(any<List<ToDoTime>>()) } returns listOf(toDoTime)
                every { toDoRepository.save(any()) } returns toDo

                changeToDoStatusUseCase.execute(updateToDoStatusReq)

                verify(exactly = 1) { toDoRepository.findByIdOrNull(todoId) }
                verify(exactly = 1) { toDoTimeRepository.deleteByToDo_Id(todoId) }
                verify(exactly = 1) { toDoTimeRepository.saveAll(any<List<ToDoTime>>()) }
                verify(exactly = 1) { toDoRepository.save(any()) }
            }

            it("시간 목록이 null이면 시간 정보는 업데이트하지 않고 상태만 변경된다") {
                val changeReqWithoutTime = UpdateToDoStatusReq(
                    id = todoId,
                    status = Status.COMPLETED,
                    timeList = null
                )
                every { toDoRepository.findByIdOrNull(todoId) } returns toDo
                every { toDoTimeRepository.deleteByToDo_Id(todoId) } just Runs
                every { toDoRepository.save(any()) } returns toDo

                changeToDoStatusUseCase.execute(changeReqWithoutTime)

                verify(exactly = 1) { toDoRepository.findByIdOrNull(todoId) }
                verify(exactly = 1) { toDoTimeRepository.deleteByToDo_Id(todoId) }
                verify(exactly = 0) { toDoTimeRepository.saveAll(any<List<ToDoTime>>()) }
                verify(exactly = 1) { toDoRepository.save(any()) }
            }

            it("여러 시간 슬롯이 있으면 모두 저장된다") {
                val multipleTimeSlots = listOf(
                    TimeSlot(
                        startTime = LocalDateTime.of(2026, 2, 5, 10, 0),
                        endTime = LocalDateTime.of(2026, 2, 5, 11, 0)
                    ),
                    TimeSlot(
                        startTime = LocalDateTime.of(2026, 2, 5, 14, 0),
                        endTime = LocalDateTime.of(2026, 2, 5, 16, 0)
                    )
                )
                val multipleTodoTimes = multipleTimeSlots.map {
                    ToDoTime(
                        startTime = it.startTime,
                        endTime = it.endTime,
                        toDo = toDo
                    )
                }
                val changeReqWithMultipleTime = UpdateToDoStatusReq(
                    id = todoId,
                    status = Status.COMPLETED,
                    timeList = multipleTimeSlots
                )
                every { toDoRepository.findByIdOrNull(todoId) } returns toDo
                every { toDoTimeRepository.deleteByToDo_Id(todoId) } just Runs
                every { toDoTimeRepository.saveAll(any<List<ToDoTime>>()) } returns multipleTodoTimes
                every { toDoRepository.save(any()) } returns toDo

                changeToDoStatusUseCase.execute(changeReqWithMultipleTime)

                verify(exactly = 1) { toDoRepository.findByIdOrNull(todoId) }
                verify(exactly = 1) { toDoTimeRepository.deleteByToDo_Id(todoId) }
                verify(exactly = 1) {
                    toDoTimeRepository.saveAll(match<List<ToDoTime>> { it.size == 2 })
                }
                verify(exactly = 1) { toDoRepository.save(any()) }
            }
        }

        context("존재하지 않는 할 일의 상태를 변경할 때") {
            val nonExistentId = 999L
            val updateToDoStatusReq = UpdateToDoStatusReq(
                id = nonExistentId,
                status = Status.COMPLETED,
                timeList = null
            )

            it("할 일을 찾을 수 없으면 ToDoNotFoundException을 던진다") {
                every { toDoRepository.findByIdOrNull(nonExistentId) } returns null

                shouldThrow<ToDoNotFoundException> {
                    changeToDoStatusUseCase.execute(updateToDoStatusReq)
                }

                verify(exactly = 1) { toDoRepository.findByIdOrNull(nonExistentId) }
                verify(exactly = 0) { toDoTimeRepository.deleteByToDo_Id(any()) }
                verify(exactly = 0) { toDoRepository.save(any()) }
            }
        }
    }
})
