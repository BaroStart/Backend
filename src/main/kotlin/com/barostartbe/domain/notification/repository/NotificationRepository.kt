package com.barostartbe.domain.notification.repository

import com.barostartbe.domain.notification.entity.Notification
import com.barostartbe.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findAllByReceiver(receiver: User): List<Notification>
    fun findTop3ByReceiverOrderByCreatedAtDesc(receiver: User): List<Notification>
}