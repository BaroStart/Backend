package com.barostartbe.domain.todo.usecase

import com.barostartbe.domain.mentee.entity.Grade
import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.mentee.entity.School
import com.barostartbe.domain.mentee.repository.MenteeRepository
import com.barostartbe.domain.todo.dto.request.CreateToDoReq
import com.barostartbe.domain.todo.entity.ToDo
import com.barostartbe.domain.todo.entity.enums.Status
import com.barostartbe.domain.todo.repository.ToDoRepository
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull

class CreateToDoUseCaseTest : DescribeSpec({

    val toDoRepository = mockk<ToDoRepository>(relaxed = true)
    val menteeRepository = mockk<MenteeRepository>(relaxed = true)
    val createToDoUseCase = CreateToDoUseCase(toDoRepository, menteeRepository)

    beforeEach {
        clearMocks(toDoRepository, menteeRepository)
    }

    describe("CreateToDoUseCase") {
        context("할 일을 생성할 때") {
            val menteeId = 1L
            val title = "국어 문제 풀기"
            val createToDoReq = CreateToDoReq(title = title)
            val mentee = Mentee(
                loginId = "testId",
                password = "password123",
                name = "홍길동",
                nickname = "길동이",
                grade = Grade.FIRST,
                school = School.NORMAL,
                hopeMajor = "컴퓨터공학"
            )
            val savedToDo = ToDo(
                title = title,
                status = Status.NOT_COMPLETED,
                mentee = mentee
            )

            it("멘티가 존재하면 할 일이 성공적으로 생성된다") {
                every { menteeRepository.findByIdOrNull(menteeId) } returns mentee
                every { toDoRepository.save(any()) } returns savedToDo

                createToDoUseCase.execute(menteeId, createToDoReq)

                verify(exactly = 1) { menteeRepository.findByIdOrNull(menteeId) }
                verify(exactly = 1) { toDoRepository.save(any()) }
            }

            it("멘티를 찾을 수 없으면 ServiceException을 던진다") {
                val nonExistentMenteeId = 999L
                every { menteeRepository.findByIdOrNull(nonExistentMenteeId) } returns null

                val exception = shouldThrow<ServiceException> {
                    createToDoUseCase.execute(nonExistentMenteeId, createToDoReq)
                }

                exception.errorCode shouldBe ErrorCode.USER_NOT_FOUND
                verify(exactly = 1) { menteeRepository.findByIdOrNull(nonExistentMenteeId) }
                verify(exactly = 0) { toDoRepository.save(any()) }
            }
        }
    }
})
