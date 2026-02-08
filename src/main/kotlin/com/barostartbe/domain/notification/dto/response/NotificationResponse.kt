package com.barostartbe.domain.notification.dto.response

import com.barostartbe.domain.notification.entity.Notification
import com.barostartbe.domain.notification.entity.enums.Type
import java.time.LocalDateTime

data class NotificationResponse(
    val id: Long,
    val title: String,
    val message: String,
    val type: Type,
    val createdAt: LocalDateTime,
    val isRead: Boolean
) {
    companion object {
        fun from(notification: Notification): NotificationResponse {
            return NotificationResponse(
                id = notification.id!!,
                title = notification.title,
                message = notification.message,
                type = notification.type,
                createdAt = notification.createdAt!!,
                isRead = notification.isRead
            )
        }
    }
}
