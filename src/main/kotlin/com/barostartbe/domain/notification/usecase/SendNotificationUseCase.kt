package com.barostartbe.domain.notification.usecase

import com.barostartbe.domain.notification.dto.request.SendNotificationRequest
import com.barostartbe.domain.notification.entity.Notification
import com.barostartbe.domain.notification.entity.enums.Type
import com.barostartbe.domain.notification.repository.NotificationRepository
import com.barostartbe.domain.user.repository.UserRepository
import com.barostartbe.global.annotation.CommandUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import com.barostartbe.global.sse.dto.base.SseEvent
import com.barostartbe.global.sse.usecase.SsePublishUseCase
import org.springframework.data.repository.findByIdOrNull

@CommandUseCase
class SendNotificationUseCase(
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository,
    private val ssePublishUseCase: SsePublishUseCase
) {
    fun execute(request: SendNotificationRequest) {
        val receiver = userRepository.findByIdOrNull(request.receiverId)
            ?: throw ServiceException(ErrorCode.USER_NOT_FOUND)

        val notification = Notification.of(
            title = request.title,
            message = request.message,
            type = request.type ?: Type.ETC,
            receiver = receiver
        )
        notificationRepository.save(notification)

        val sseEvent = SseEvent(
            topic = "user_${receiver.id}",
            event = "notification",
            data = notification
        )
        ssePublishUseCase.publishToTopic(sseEvent.topic!!, sseEvent)
    }
}
