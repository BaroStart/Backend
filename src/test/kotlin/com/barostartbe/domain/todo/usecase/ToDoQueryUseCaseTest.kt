package com.barostartbe.domain.todo.usecase

import com.barostartbe.domain.todo.repository.ToDoRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk

class ToDoQueryUseCaseTest : DescribeSpec({

    val toDoRepository = mockk<ToDoRepository>(relaxed = true)
    val toDoQueryUseCase = ToDoQueryUseCase(toDoRepository)

    beforeEach {
        clearMocks(toDoRepository)
    }

    describe("ToDoQueryUseCase") {
        val menteeId = 1L

        context("6시~9시 사이 공부 개수 조회") {
            it("6시~9시 사이에 시작하고 종료된 공부만 카운트한다") {
                every { toDoRepository.countStudyBetweenSixAndNine(menteeId) } returns 1

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

        context("25분 이상 공부 완료 개수 조회") {
            it("누적 시간이 25분 이상인 할 일만 카운트한다") {
                every { toDoRepository.countCompletedOver25Minutes(menteeId) } returns 1

                toDoQueryUseCase.getCompletedOver25MinutesCount(menteeId) shouldBe 1
            }
        }
    }
})
