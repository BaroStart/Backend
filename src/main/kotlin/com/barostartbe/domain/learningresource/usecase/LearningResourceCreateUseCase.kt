package com.barostartbe.domain.learningresource.usecase

import com.barostartbe.domain.learningresource.dto.request.LearningResourceCreateReq
import com.barostartbe.domain.learningresource.dto.response.LearningResourceListItemRes
import com.barostartbe.domain.learningresource.entity.LearningResource
import com.barostartbe.domain.learningresource.entity.LearningResourceCreatorType
import com.barostartbe.domain.learningresource.repository.LearningResourceRepository
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.global.annotation.CommandUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode

// 학습 자료 생성 Use Case
@CommandUseCase
class LearningResourceCreateUseCase(
    private val learningResourceRepository: LearningResourceRepository
) {

    fun create(mentor: Mentor, req: LearningResourceCreateReq): LearningResourceListItemRes {
        // 멘토 인증/유효성 체크
        if (mentor.id == null) {
            throw ServiceException(ErrorCode.NO_AUTH)
        }

        // 학습 자료 엔티티 생성
        val resource = LearningResource(
            fileName = req.fileName,
            subject = req.subject,
            fileUrl = req.fileUrl,
            fileSize = req.fileSize,
            creatorType = LearningResourceCreatorType.MENTOR, // 멘토 고정
            mentor = mentor
        )

        // 저장
        val saved = learningResourceRepository.save(resource)

        return LearningResourceListItemRes.from(saved)
    }
}
