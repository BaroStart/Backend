package com.barostartbe.domain.todo.usecase

import com.barostartbe.domain.mentee.entity.Grade
import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.mentee.entity.School
import com.barostartbe.domain.todo.entity.ToDo
import com.barostartbe.domain.todo.entity.ToDoTime
import com.barostartbe.domain.todo.entity.enums.Status
import com.barostartbe.domain.todo.repository.ToDoRepository
import com.barostartbe.domain.todo.repository.ToDoTimeRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import java.time.LocalDateTime

class GetTodayToDoListUseCaseTest : DescribeSpec({

    val toDoRepository = mockk<ToDoRepository>(relaxed = true)
    val toDoTimeRepository = mockk<ToDoTimeRepository>(relaxed = true)
    val getTodayToDoListUseCase = GetTodayToDoListUseCase(toDoRepository, toDoTimeRepository)

    beforeEach {
        clearMocks(toDoRepository, toDoTimeRepository)
    }

    describe("GetTodayToDoListUseCase") {
        context("오늘의 할 일 목록을 조회할 때") {
            val menteeId = 1L
            val mentee = Mentee(
                loginId = "testId",
                password = "password123",
                name = "홍길동",
                nickname = "길동이",
                grade = Grade.FIRST,
                school = School.NORMAL,
                hopeMajor = "컴퓨터공학"
            )

            it("할 일이 여러 개 있으면 모든 할 일을 반환한다") {
                val todo1 = mockk<ToDo> {
                    every { id } returns 1L
                    every { title } returns "국어 문제 풀기"
                    every { status } returns Status.NOT_COMPLETED
                    every { this@mockk.mentee } returns mentee
                }
                val todo2 = mockk<ToDo> {
                    every { id } returns 2L
                    every { title } returns "영어 단어 외우기"
                    every { status } returns Status.COMPLETED
                    every { this@mockk.mentee } returns mentee
                }
                val todo3 = mockk<ToDo> {
                    every { id } returns 3L
                    every { title } returns "수학 문제 풀기"
                    every { status } returns Status.NOT_COMPLETED
                    every { this@mockk.mentee } returns mentee
                }

                val todoList = listOf(todo1, todo2, todo3)
                val timeList1 = listOf(
                    ToDoTime(
                        startTime = LocalDateTime.of(2026, 2, 5, 10, 0),
                        endTime = LocalDateTime.of(2026, 2, 5, 11, 0),
                        toDo = todo1
                    )
                )
                val timeList2 = listOf(
                    ToDoTime(
                        startTime = LocalDateTime.of(2026, 2, 5, 14, 0),
                        endTime = LocalDateTime.of(2026, 2, 5, 15, 0),
                        toDo = todo2
                    )
                )
                val timeList3 = emptyList<ToDoTime>()

                every { toDoRepository.findAllByMenteeIdAndCreatedDate(menteeId, LocalDate.now()) } returns todoList
                every { toDoTimeRepository.findByToDo_Id(1L) } returns timeList1
                every { toDoTimeRepository.findByToDo_Id(2L) } returns timeList2
                every { toDoTimeRepository.findByToDo_Id(3L) } returns timeList3

                val result = getTodayToDoListUseCase.execute(menteeId)

                result shouldHaveSize 3
                result[0].title shouldBe "국어 문제 풀기"
                result[0].status shouldBe Status.NOT_COMPLETED
                result[0].timeList?.shouldHaveSize(1)
                result[1].title shouldBe "영어 단어 외우기"
                result[1].status shouldBe Status.COMPLETED
                result[1].timeList?.shouldHaveSize(1)
                result[2].title shouldBe "수학 문제 풀기"
                result[2].status shouldBe Status.NOT_COMPLETED
                result[2].timeList.shouldBeEmpty()

                verify(exactly = 1) { toDoRepository.findAllByMenteeIdAndCreatedDate(menteeId, LocalDate.now()) }
                verify(exactly = 1) { toDoTimeRepository.findByToDo_Id(1L) }
                verify(exactly = 1) { toDoTimeRepository.findByToDo_Id(2L) }
                verify(exactly = 1) { toDoTimeRepository.findByToDo_Id(3L) }
            }

            it("할 일이 하나도 없으면 빈 리스트를 반환한다") {
                every { toDoRepository.findAllByMenteeIdAndCreatedDate(menteeId, LocalDate.now()) } returns emptyList()

                val result = getTodayToDoListUseCase.execute(menteeId)

                result.shouldBeEmpty()
                verify(exactly = 1) { toDoRepository.findAllByMenteeIdAndCreatedDate(menteeId, LocalDate.now()) }
                verify(exactly = 0) { toDoTimeRepository.findByToDo_Id(any()) }
            }

            it("할 일에 여러 시간 슬롯이 있으면 모두 포함하여 반환한다") {
                val todo = mockk<ToDo> {
                    every { id } returns 1L
                    every { title } returns "국어 문제 풀기"
                    every { status } returns Status.COMPLETED
                    every { this@mockk.mentee } returns mentee
                }
                val timeList = listOf(
                    ToDoTime(
                        startTime = LocalDateTime.of(2026, 2, 5, 10, 0),
                        endTime = LocalDateTime.of(2026, 2, 5, 11, 0),
                        toDo = todo
                    ),
                    ToDoTime(
                        startTime = LocalDateTime.of(2026, 2, 5, 14, 0),
                        endTime = LocalDateTime.of(2026, 2, 5, 16, 0),
                        toDo = todo
                    ),
                    ToDoTime(
                        startTime = LocalDateTime.of(2026, 2, 5, 18, 0),
                        endTime = LocalDateTime.of(2026, 2, 5, 19, 0),
                        toDo = todo
                    )
                )

                every { toDoRepository.findAllByMenteeIdAndCreatedDate(menteeId, LocalDate.now()) } returns listOf(todo)
                every { toDoTimeRepository.findByToDo_Id(1L) } returns timeList

                val result = getTodayToDoListUseCase.execute(menteeId)

                result shouldHaveSize 1
                result[0].title shouldBe "국어 문제 풀기"
                result[0].status shouldBe Status.COMPLETED
                result[0].timeList?.shouldHaveSize(3)
                verify(exactly = 1) { toDoRepository.findAllByMenteeIdAndCreatedDate(menteeId, LocalDate.now()) }
                verify(exactly = 1) { toDoTimeRepository.findByToDo_Id(1L) }
            }
        }
    }
})
