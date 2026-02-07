package com.barostartbe.domain.badge.controller

import com.barostartbe.domain.badge.dto.response.BadgeInfoResponse
import com.barostartbe.domain.badge.usecase.MenteeBadgeCommandUseCase
import com.barostartbe.domain.badge.usecase.MenteeBadgeQueryUseCase
import com.barostartbe.domain.user.entity.User
import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.response.type.SuccessCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class BadgeController(
    private val menteeBadgeQueryUseCase: MenteeBadgeQueryUseCase
) : BadgeApi {

    override fun getMyBadges(user: User): ResponseEntity<ApiResponse<List<BadgeInfoResponse>>> {
        val badges = menteeBadgeQueryUseCase.getAllByMentee(user.id!!)
        return ApiResponse.success(SuccessCode.REQUEST_OK, badges)
    }
}
