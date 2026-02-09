package com.barostartbe.domain.feedback.controller

import com.barostartbe.domain.assignment.entity.enums.Subject
import com.barostartbe.domain.feedback.dto.request.FeedbackCreateReq
import com.barostartbe.domain.feedback.dto.response.FeedbackListItemRes
import com.barostartbe.domain.feedback.dto.response.DailyFeedbackSummaryRes
import com.barostartbe.domain.feedback.dto.response.DailyFeedbackRes
import com.barostartbe.domain.feedback.dto.response.MenteeFeedbackDetailRes
import com.barostartbe.domain.feedback.entity.enums.FeedbackStatus
import com.barostartbe.domain.feedback.usecase.DailyFeedbackQueryUseCase
import com.barostartbe.domain.feedback.usecase.FeedbackCreateUseCase
import com.barostartbe.domain.feedback.usecase.FeedbackListQueryUseCase
import com.barostartbe.domain.feedback.usecase.DailyFeedbackSummaryQueryUseCase
import com.barostartbe.domain.user.entity.User
import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.response.type.SuccessCode
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class FeedbackController(
    private val feedbackListQueryUseCase: FeedbackListQueryUseCase,
    private val feedbackCreateUseCase: FeedbackCreateUseCase,
    private val dailyFeedbackSummaryQueryUseCase: DailyFeedbackSummaryQueryUseCase,
    private val dailyFeedbackQueryUseCase: DailyFeedbackQueryUseCase
) : FeedbackApi {

    override fun getListByMentor(@AuthenticationPrincipal mentor: User,
                                 @RequestParam(required = false) status: FeedbackStatus?
    ): ResponseEntity<ApiResponse<List<FeedbackListItemRes>>> {
        val mentorId = mentor.id!!
        return ApiResponse.success(SuccessCode.REQUEST_OK, feedbackListQueryUseCase.getListByMentor(mentorId = mentorId, status = status))
    }

    override fun createFeedback(@AuthenticationPrincipal mentor: User, assignmentId: Long, req: FeedbackCreateReq
    ): ResponseEntity<ApiResponse<Long>> {

        val feedbackId = feedbackCreateUseCase.create(
            mentorId = mentor.id!!,
            assignmentId = assignmentId,
            req = req
        )
        return ApiResponse.success(SuccessCode.CREATE_OK, feedbackId)
    }

    override fun getDailyFeedbackSummaries(@AuthenticationPrincipal mentee: User
    ): ResponseEntity<ApiResponse<List<DailyFeedbackSummaryRes>>> {

        val result =
            dailyFeedbackSummaryQueryUseCase.getDailySummaries(mentee.id!!)

        return ApiResponse.success(
            SuccessCode.REQUEST_OK,
            result
        )
    }

    override fun getDailyFeedbacks(@AuthenticationPrincipal mentee: User, subject: Subject?
    ): ResponseEntity<ApiResponse<List<DailyFeedbackRes>>> {

        val result = dailyFeedbackQueryUseCase.getDailyFeedbacks(
            menteeId = mentee.id!!,
            subject = subject
        )

        return ApiResponse.success(
            SuccessCode.REQUEST_OK,
            result
        )
    }

    override fun getMenteeFeedbackDetail(@AuthenticationPrincipal mentee: User, assignmentId: Long
    ): ResponseEntity<ApiResponse<MenteeFeedbackDetailRes>> {

        val result = feedbackListQueryUseCase.getDetail(
            menteeId = mentee.id!!,
            assignmentId = assignmentId
        )

        return ApiResponse.success(SuccessCode.REQUEST_OK, result)
    }
}
