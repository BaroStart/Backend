package com.barostartbe.domain.overall.usecase

import com.barostartbe.domain.overall.dto.response.OverallResponse
import com.barostartbe.domain.overall.repository.OverallRepository
import com.barostartbe.global.annotation.QueryUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import java.time.LocalDate

@QueryUseCase
class OverallQueryUseCase(
    private val overallRepository: OverallRepository
) {
    fun getForMentee(menteeId: Long, date: LocalDate): List<OverallResponse> {
        val overalls = overallRepository.findAllByMenteeIdAndDate(menteeId, date)
        if (overalls.isEmpty()) throw ServiceException(ErrorCode.OVERALL_NOT_FOUND)
        return overalls.map { OverallResponse.from(it) }
    }

    fun getForMentor(menteeId: Long, mentorId: Long, date: LocalDate): OverallResponse {
        val overall = overallRepository.findByMenteeIdAndMentorIdAndDate(menteeId, mentorId, date)
            ?: throw ServiceException(ErrorCode.OVERALL_NOT_FOUND)
        return OverallResponse.from(overall)
    }
}