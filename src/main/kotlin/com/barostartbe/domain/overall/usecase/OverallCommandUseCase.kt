package com.barostartbe.domain.overall.usecase

import com.barostartbe.domain.mentee.repository.MenteeRepository
import com.barostartbe.domain.mentor.repository.MentorRepository
import com.barostartbe.domain.notification.dto.request.SendNotificationRequest
import com.barostartbe.domain.notification.entity.enums.Type
import com.barostartbe.domain.notification.usecase.SendNotificationUseCase
import com.barostartbe.domain.overall.dto.request.OverallCreateRequest
import com.barostartbe.domain.overall.entity.Overall
import com.barostartbe.domain.overall.repository.OverallRepository
import com.barostartbe.global.annotation.CommandUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import org.springframework.data.repository.findByIdOrNull

@CommandUseCase
class OverallCommandUseCase(
    private val overallRepository: OverallRepository,
    private val menteeRepository: MenteeRepository,
    private val mentorRepository: MentorRepository,
    private val sendNotificationUseCase: SendNotificationUseCase
) {

    fun create(overallCreateRequest: OverallCreateRequest, mentorId: Long) {
        val mentor = mentorRepository.findByIdOrNull(mentorId) ?: throw ServiceException(ErrorCode.MENTOR_NOT_FOUND)
        val mentee = menteeRepository.findByIdOrNull(overallCreateRequest.menteeId)
            ?: throw ServiceException(ErrorCode.MENTEE_NOT_FOUND)

        val overall = Overall.of(
            overallCreateRequest.content,
            mentee,
            mentor
        )

        overallRepository.save(overall)

        sendNotificationUseCase.execute(
            SendNotificationRequest(
                title = Type.OVERALL.titleFormat,
                message = String.format(Type.OVERALL.messageFormat, mentor.name),
                receiverId = overallCreateRequest.menteeId,
                type = Type.OVERALL
            )
        )
    }
}
