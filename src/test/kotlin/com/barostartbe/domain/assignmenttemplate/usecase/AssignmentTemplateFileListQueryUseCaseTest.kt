package com.barostartbe.domain.assignmenttemplate.usecase

import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.assignmenttemplate.entity.AssignmentTemplate
import com.barostartbe.domain.assignmenttemplate.entity.AssignmentTemplateFile
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateFileRepository
import com.barostartbe.domain.mentor.entity.Mentor
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*

class AssignmentTemplateFileListQueryUseCaseTest : DescribeSpec({

    val assignmentTemplateFileRepository =
        mockk<AssignmentTemplateFileRepository>(relaxed = true)

    val useCase = AssignmentTemplateFileListQueryUseCase(
        assignmentTemplateFileRepository
    )

    beforeEach {
        clearAllMocks()
    }

    describe("AssignmentTemplateFileListQueryUseCase.execute") {

        it("과목 필터 없이 멘토의 템플릿 파일 목록을 조회한다") {
            // given
            val mentor = mockk<Mentor>(relaxed = true)

            val template = mockk<AssignmentTemplate>(relaxed = true) {
                every { subject } returns Subject.KOREAN
            }

            val file = mockk<AssignmentTemplateFile>(relaxed = true) {
                every { assignmentTemplate } returns template
                every { fileName } returns "file.pdf"
                every { url } returns "url"
            }

            every {
                assignmentTemplateFileRepository
                    .findAllByMentorOrderByCreatedAtDesc(mentor)
            } returns listOf(file)

            // when
            val result = useCase.execute(mentor, null)

            // then
            result.size shouldBe 1
        }

        it("과목 필터로 멘토의 템플릿 파일 목록을 조회한다") {
            // given
            val mentor = mockk<Mentor>(relaxed = true)

            val template = mockk<AssignmentTemplate>(relaxed = true) {
                every { subject } returns Subject.MATH
            }

            val file = mockk<AssignmentTemplateFile>(relaxed = true) {
                every { assignmentTemplate } returns template
                every { fileName } returns "math.pdf"
                every { url } returns "url"
            }

            every {
                assignmentTemplateFileRepository
                    .findAllByMentorAndSubjectOrderByCreatedAtDesc(
                        mentor,
                        Subject.MATH
                    )
            } returns listOf(file)

            // when
            val result = useCase.execute(mentor, Subject.MATH)

            // then
            result.size shouldBe 1
        }
    }
})
