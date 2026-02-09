package com.barostartbe.domain.notification.usecase

import com.barostartbe.domain.notification.repository.NotificationRepository
import com.barostartbe.global.annotation.CommandUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import org.springframework.data.repository.findByIdOrNull

@CommandUseCase
class ReadNotificationUseCase(
    private val notificationRepository: NotificationRepository
) {
    fun execute(userId: Long, notificationId: Long) {
        val notification = notificationRepository.findByIdOrNull(notificationId)
            ?: throw ServiceException(ErrorCode.NOTIFICATION_NOT_FOUND)

        if (notification.receiver.id != userId) {
            throw ServiceException(ErrorCode.NOTIFICATION_READ_FORBIDDEN)
        }

        notification.read()
    }
}
