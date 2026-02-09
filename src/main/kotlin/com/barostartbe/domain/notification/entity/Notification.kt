package com.barostartbe.domain.notification.entity

import com.barostartbe.domain.notification.entity.enums.Type
import com.barostartbe.domain.user.entity.User
import com.barostartbe.global.common.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "notifications")
class Notification(

    @Column(nullable = false, length = 50)
    val title: String,

    @Column(nullable = false)
    val message: String,

    @Column(nullable = false)
    var isRead: Boolean = false,

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    val type: Type,

    @JoinColumn(name = "receiver_id", nullable = false)
    @ManyToOne
    val receiver: User

) : BaseEntity() {

    fun read() {
        this.isRead = true
    }

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
