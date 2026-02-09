package com.barostartbe.domain.notification.usecase

import com.barostartbe.domain.notification.dto.response.NotificationResponse
import com.barostartbe.domain.notification.repository.NotificationRepository
import com.barostartbe.domain.user.repository.UserRepository
import com.barostartbe.global.annotation.QueryUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import org.springframework.data.repository.findByIdOrNull


@QueryUseCase
class GetNotificationUseCase(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository
) {
    fun getAll(userId: Long): List<NotificationResponse> {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw ServiceException(ErrorCode.USER_NOT_FOUND)

        return notificationRepository.findAllByReceiver(user)
            .map { NotificationResponse.from(it) }
    }

    fun getRecent(userId: Long): List<NotificationResponse> {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw ServiceException(ErrorCode.USER_NOT_FOUND)

        return notificationRepository.findTop3ByReceiverOrderByCreatedAtDesc(user)
            .map { NotificationResponse.from(it) }
    }
}
