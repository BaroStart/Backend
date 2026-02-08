package com.barostartbe.domain.mentee.dto

data class GetMentoMainDashboardResponseDto(
    val totalInfo: GetTotalMenteeInfoResponsesDto,
    val menteeInfoList: List<GetMenteeInfoResponseDto>
)
