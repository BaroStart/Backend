package com.barostartbe.domain.todo.usecase

import com.barostartbe.domain.todo.entity.ToDo
import com.barostartbe.domain.todo.entity.ToDoTime
import com.barostartbe.domain.todo.entity.enums.Status
import com.barostartbe.domain.todo.repository.ToDoRepository
import com.barostartbe.domain.todo.repository.ToDoTimeRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime

class ToDoQueryUseCaseTest : DescribeSpec({

    val toDoRepository = mockk<ToDoRepository>(relaxed = true)
    val toDoTimeRepository = mockk<ToDoTimeRepository>(relaxed = true)
    val toDoQueryUseCase = ToDoQueryUseCase(toDoRepository, toDoTimeRepository)

    beforeEach {
        clearMocks(toDoRepository, toDoTimeRepository)
    }

    describe("ToDoQueryUseCase") {
        val menteeId = 1L

        context("6시~9시 사이 공부 개수 조회") {
            it("6시~9시 사이에 시작하고 종료된 공부만 카운트한다") {
                val todo1 = mockk<ToDo> { every { id } returns 1L }
                val time1 = listOf(
                    ToDoTime(
                        startTime = LocalDateTime.of(2026, 2, 7, 6, 0),
                        endTime = LocalDateTime.of(2026, 2, 7, 8, 59),
                        toDo = todo1
                    )
                )

                val todo2 = mockk<ToDo> { every { id } returns 2L }
                val time2 = listOf(
                    ToDoTime(
                        startTime = LocalDateTime.of(2026, 2, 7, 8, 0),
                        endTime = LocalDateTime.of(2026, 2, 7, 9, 1), // 9시 이후 종료 (제외)
                        toDo = todo2
                    )
                )

                val todo3 = mockk<ToDo> { every { id } returns 3L }
                val time3 = listOf(
                    ToDoTime(
                        startTime = LocalDateTime.of(2026, 2, 7, 5, 59), // 6시 이전 시작 (제외)
                        endTime = LocalDateTime.of(2026, 2, 7, 7, 0),
                        toDo = todo3
                    )
                )

                every { toDoRepository.findAllByMentee_IdAndStatus(menteeId, Status.COMPLETED) } returns listOf(todo1, todo2, todo3)
                every { toDoTimeRepository.findByToDo_Id(1L) } returns time1
                every { toDoTimeRepository.findByToDo_Id(2L) } returns time2
                every { toDoTimeRepository.findByToDo_Id(3L) } returns time3

                toDoQueryUseCase.getStudyBetweenSixAndNineCount(menteeId) shouldBe 1
            }
        }

        context("7일 연속 완료 스트릭 확인") {
            it("스트릭이 7일 이상이면 true를 반환한다") {
                every { toDoRepository.findMaxConsecutivePerfectDays(menteeId) } returns 7
                toDoQueryUseCase.is7DaysToDoCompletedStreak(menteeId) shouldBe true
            }

            it("스트릭이 7일 미만이면 false를 반환한다") {
                every { toDoRepository.findMaxConsecutivePerfectDays(menteeId) } returns 6
                toDoQueryUseCase.is7DaysToDoCompletedStreak(menteeId) shouldBe false
            }
        }

        context("20분 이상 공부 완료 개수 조회") {
            it("누적 시간이 20분 이상인 할 일만 카운트한다") {
                val todo1 = mockk<ToDo> { every { id } returns 1L }
                val time1 = listOf(
                    ToDoTime(
                        startTime = LocalDateTime.of(2026, 2, 7, 10, 0),
                        endTime = LocalDateTime.of(2026, 2, 7, 10, 20), // 20분
                        toDo = todo1
                    )
                )

                val todo2 = mockk<ToDo> { every { id } returns 2L }
                val time2 = listOf(
                    ToDoTime(
                        startTime = LocalDateTime.of(2026, 2, 7, 11, 0),
                        endTime = LocalDateTime.of(2026, 2, 7, 11, 19), // 19분
                        toDo = todo2
                    )
                )

                every { toDoRepository.findAllByMentee_IdAndStatus(menteeId, Status.COMPLETED) } returns listOf(todo1, todo2)
                every { toDoTimeRepository.findByToDo_Id(1L) } returns time1
                every { toDoTimeRepository.findByToDo_Id(2L) } returns time2

                toDoQueryUseCase.getCompletedOver25MinutesCount(menteeId) shouldBe 1
            }
        }
    }
})
