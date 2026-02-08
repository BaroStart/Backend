package com.barostartbe.domain.assignmenttemplate.usecase

import com.barostartbe.domain.assignmenttemplate.entity.AssignmentTemplate
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateFileRepository
import com.barostartbe.domain.assignmenttemplate.repository.AssignmentTemplateRepository
import com.barostartbe.domain.mentor.entity.Mentor
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.mockk.*
import java.util.*

class AssignmentTemplateDeleteUseCaseTest : DescribeSpec({

    val assignmentTemplateRepository = mockk<AssignmentTemplateRepository>(relaxed = true)
    val assignmentTemplateFileRepository = mockk<AssignmentTemplateFileRepository>(relaxed = true)

    val useCase = AssignmentTemplateDeleteUseCase(
        assignmentTemplateRepository,
        assignmentTemplateFileRepository
    )

    beforeEach {
        clearAllMocks()
    }

    describe("AssignmentTemplateDeleteUseCase.execute") {

        it("멘토가 자신의 템플릿을 정상 삭제한다") {
            // given
            val mentor = mockk<Mentor>(relaxed = true) {
                every { id } returns 1L
            }

            val template = mockk<AssignmentTemplate>(relaxed = true) {
                every { id } returns 1L
                every { this@mockk.mentor } returns mentor
            }

            every {
                assignmentTemplateRepository.findById(1L)
            } returns Optional.of(template)

            every {
                assignmentTemplateFileRepository.deleteAllByAssignmentTemplate(template)
            } just Runs

            every {
                assignmentTemplateRepository.delete(template)
            } just Runs

            // when / then
            shouldNotThrowAny {
                useCase.execute(mentor, 1L)
            }
        }
    }
})
