package com.barostartbe.domain.feedbacktemplate.usecase

import com.barostartbe.domain.feedbacktemplate.dto.request.FeedbackTemplateCreateReq
import com.barostartbe.domain.feedbacktemplate.entity.FeedbackTemplate
import com.barostartbe.domain.feedbacktemplate.repository.FeedbackTemplateRepository
import com.barostartbe.domain.assignment.entity.enum.Subject
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class FeedbackTemplateCreateUseCaseTest : DescribeSpec({

    val feedbackTemplateRepository = mockk<FeedbackTemplateRepository>()
    val useCase = FeedbackTemplateCreateUseCase(feedbackTemplateRepository)

    describe("FeedbackTemplateCreateUseCase") {

        it("피드백 템플릿을 생성한다") {
            // given
            val req = FeedbackTemplateCreateReq(
                name = "기본 피드백 템플릿",
                subject = Subject.KOREAN,
                content = "잘했어요."
            )

            val savedTemplate = FeedbackTemplate(
                name = req.name,
                subject = req.subject,
                content = req.content
            )

            every { feedbackTemplateRepository.save(any()) } returns savedTemplate

            // when
            val result = useCase.create(req)

            // then
            result shouldBe savedTemplate
        }
    }
})
