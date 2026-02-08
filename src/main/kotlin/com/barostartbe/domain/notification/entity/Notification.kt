package com.barostartbe.domain.notification.entity

import com.barostartbe.domain.notification.entity.enums.Type
import com.barostartbe.domain.user.entity.User
import com.barostartbe.global.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "notifications")
class Notification(

    @Column(nullable = false, length = 50)
    val title: String,

    @Column(nullable = false)
    val message: String,

    @Column(nullable = false)
    val isRead: Boolean = false,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val type: Type,

    @JoinColumn(name = "receiver_id", nullable = false)
    @ManyToOne
    val receiver: User

) : BaseEntity() {

    companion object {
        fun of(title: String, message: String, type: Type, receiver: User): Notification {
            return Notification(
                title = title,
                message = message,
                type = type,
                receiver = receiver
            )
        }
    }
}
