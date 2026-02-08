package com.barostartbe.domain.assignmenttemplate.usecase

import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.assignmenttemplate.dto.request.AssignmentTemplateFileReq
import com.barostartbe.domain.assignmenttemplate.dto.request.AssignmentTemplateUpdateReq
import com.barostartbe.domain.assignmenttemplate.entity.AssignmentTemplate
import com.barostartbe.domain.assignmenttemplate.entity.AssignmentTemplateFile
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateFileRepository
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateRepository
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.global.error.exception.ServiceException
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.util.Optional

class AssignmentTemplateUpdateUseCaseTest : DescribeSpec({

    val assignmentTemplateRepository =
        mockk<AssignmentTemplateRepository>(relaxed = true)

    val assignmentTemplateFileRepository =
        mockk<AssignmentTemplateFileRepository>(relaxed = true)

    val useCase = AssignmentTemplateUpdateUseCase(
        assignmentTemplateRepository,
        assignmentTemplateFileRepository
    )

    beforeEach {
        clearAllMocks()
    }

    describe("AssignmentTemplateUpdateUseCase.execute") {

        it("멘토가 자신의 템플릿을 정상 수정한다") {
            // given
            val mentor = mockk<Mentor> {
                every { id } returns 1L
            }

            val template = mockk<AssignmentTemplate>(relaxed = true) {
                every { id } returns 10L
                every { subject } returns Subject.KOREAN
                every { this@mockk.mentor } returns mentor
            }

            val req = AssignmentTemplateUpdateReq(
                subject = Subject.KOREAN, // ✅ 필수
                name = "수정 이름",
                description = "수정 설명",
                title = "수정 제목",
                content = "수정 내용",
                files = listOf(
                    AssignmentTemplateFileReq(
                        fileName = "file.pdf",
                        url = "url"
                    )
                )
            )

            every {
                assignmentTemplateRepository.findById(10L)
            } returns Optional.of(template)

            every {
                assignmentTemplateFileRepository.findAllByAssignmentTemplate(template)
            } returns listOf(
                mockk<AssignmentTemplateFile>(relaxed = true) {
                    every { fileName } returns "file.pdf"
                    every { url } returns "url"
                }
            )

            // when
            val result = useCase.execute(
                mentor = mentor,
                templateId = 10L,
                req = req
            )

            // then
            result.id shouldBe 10L
            result.files.size shouldBe 1
        }

        it("다른 멘토의 템플릿이면 예외를 던진다") {
            // given
            val mentor = mockk<Mentor> { every { id } returns 1L }
            val otherMentor = mockk<Mentor> { every { id } returns 2L }

            val template = mockk<AssignmentTemplate>(relaxed = true) {
                every { this@mockk.mentor } returns otherMentor
            }

            val req = AssignmentTemplateUpdateReq(
                subject = Subject.KOREAN,
                name = "수정",
                description = "수정",
                title = "수정",
                content = "수정",
                files = null
            )

            every {
                assignmentTemplateRepository.findById(any())
            } returns Optional.of(template)

            // when / then
            shouldThrow<ServiceException> {
                useCase.execute(mentor, 1L, req)
            }
        }

        it("템플릿이 없으면 예외를 던진다") {
            // given
            val mentor = mockk<Mentor>(relaxed = true)

            val req = AssignmentTemplateUpdateReq(
                subject = Subject.KOREAN,
                name = "수정",
                description = "수정",
                title = "수정",
                content = "수정",
                files = null
            )

            every {
                assignmentTemplateRepository.findById(any())
            } returns Optional.empty()

            // when / then
            shouldThrow<ServiceException> {
                useCase.execute(mentor, 999L, req)
            }
        }
    }
})
