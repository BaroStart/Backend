package com.barostartbe.domain.learningresource.usecase

import com.barostartbe.domain.learningresource.dto.response.LearningResourceListItemRes
import com.barostartbe.domain.learningresource.repository.LearningResourceRepository
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.global.annotation.QueryUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode

/**
 * 학습 자료 목록 조회 유스케이스
 */
@QueryUseCase
class LearningResourceListQueryUseCase(
    private val learningResourceRepository: LearningResourceRepository
) {

    fun getList(mentor: Mentor): List<LearningResourceListItemRes> {
        // 멘토 본인 체크
        if (mentor.id == null) {
            throw ServiceException(ErrorCode.NO_AUTH)
        }

        // 멘토 기준 최신 등록순 조회
        val resources = learningResourceRepository
            .findAllByMentorOrderByCreatedAtDesc(mentor)

        return resources.map { LearningResourceListItemRes.from(it) }
    }
}
