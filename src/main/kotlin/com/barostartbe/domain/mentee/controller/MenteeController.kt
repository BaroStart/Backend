package com.barostartbe.domain.mentee.controller

import com.barostartbe.domain.mentee.dto.CalendarResponseDto
import com.barostartbe.domain.mentee.dto.GetMenteeInfoResponseDto
import com.barostartbe.domain.mentee.dto.TaskInfoResponseDto
import com.barostartbe.domain.mentee.dto.GetMenteeBasicInfoResponseDto
import com.barostartbe.domain.mentee.dto.GetMenteeDashboardResponseDto
import com.barostartbe.domain.mentee.dto.GetMentoMainDashboardResponseDto
import com.barostartbe.domain.mentee.usecase.MenteeQueryUseCase
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.domain.user.entity.User
import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.response.type.SuccessCode
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class MenteeController(
    val menteeQueryUseCase: MenteeQueryUseCase
) : MenteeApi {

    override fun getMenteeInfo(
        @PathVariable menteeId: Long,
        @AuthenticationPrincipal mentor: User
    ): ResponseEntity<ApiResponse<GetMenteeBasicInfoResponseDto>> {
        return ApiResponse.success(SuccessCode.REQUEST_OK, menteeQueryUseCase.getMenteeInfo(mentor.id!!, menteeId))
    }

    override fun getTimeTable(
        @AuthenticationPrincipal mentee: User,
        date: LocalDate?
    ): ResponseEntity<ApiResponse<List<TaskInfoResponseDto>>> {
        return ApiResponse.success(
            SuccessCode.REQUEST_OK,
            menteeQueryUseCase.getTimeTable(mentee.id!!, date ?: LocalDate.now())
        )
    }

    override fun getCalendar(
        @AuthenticationPrincipal mentee: User,
        @RequestParam year: Int,
        @RequestParam month: Int
    ): ResponseEntity<ApiResponse<List<CalendarResponseDto>>> {
        return ApiResponse.success(
            SuccessCode.REQUEST_OK,
            menteeQueryUseCase.getCalendar(mentee.id!!, year, month)
        )
    }

    override fun getMenteeDetailsDashboard(
        menteeId: Long,
        searchType: String,
        date: String?,
        mentor: User
    ): ResponseEntity<ApiResponse<GetMenteeDashboardResponseDto>> {
        val response = menteeQueryUseCase.getMenteeDashboard(mentor as Mentor, menteeId, searchType, date)
        return ApiResponse.success(SuccessCode.REQUEST_OK, response)
    }

    override fun getMentorMainDashboard(mentorId: Long): ResponseEntity<ApiResponse<GetMentoMainDashboardResponseDto>>
        = ApiResponse.success(SuccessCode.REQUEST_OK, menteeQueryUseCase.getMenteesTodayDetails(mentorId))
}