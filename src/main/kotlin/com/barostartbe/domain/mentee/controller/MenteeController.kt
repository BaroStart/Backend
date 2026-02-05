package com.barostartbe.domain.mentee.controller

import com.barostartbe.domain.mentee.dto.GetMenteeInfoResponseDto
import com.barostartbe.domain.mentee.usecase.MenteeQueryUseCase
import com.barostartbe.domain.user.entity.User
import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.response.type.SuccessCode
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class MenteeController(
    val  menteeQueryUseCase: MenteeQueryUseCase
): MenteeApi{

    override fun getMenteeInfo(@PathVariable menteeId: Long, @AuthenticationPrincipal mentor: User): ResponseEntity<ApiResponse<GetMenteeInfoResponseDto>> {
        return ApiResponse.success(SuccessCode.REQUEST_OK, menteeQueryUseCase.getMenteeInfo(mentor.id!!, menteeId))
    }
}