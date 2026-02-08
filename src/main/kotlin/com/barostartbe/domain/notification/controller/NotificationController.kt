package com.barostartbe.domain.notification.controller

import com.barostartbe.domain.notification.dto.request.NoticeRequest
import com.barostartbe.domain.notification.dto.request.SendNotificationRequest
import com.barostartbe.domain.notification.dto.response.NotificationResponse
import com.barostartbe.domain.notification.usecase.GetNotificationUseCase
import com.barostartbe.domain.notification.usecase.ReadNotificationUseCase
import com.barostartbe.domain.notification.usecase.SendNoticeUseCase
import com.barostartbe.domain.notification.usecase.SendNotificationUseCase
import com.barostartbe.domain.user.entity.User
import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.response.type.SuccessCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class NotificationController(
    private val readNotificationUseCase: ReadNotificationUseCase,
    private val getNotificationUseCase: GetNotificationUseCase,
    private val sendNoticeUseCase: SendNoticeUseCase,
    private val sendNotificationUseCase: SendNotificationUseCase
) : NotificationApi {

    override fun readNotification(
        user: User,
        notificationId: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        readNotificationUseCase.execute(user.id!!, notificationId)
        return ApiResponse.success(SuccessCode.REQUEST_OK)
    }

    override fun getNotifications(
        user: User
    ): ResponseEntity<ApiResponse<List<NotificationResponse>>> {
        return ApiResponse.success(SuccessCode.REQUEST_OK, getNotificationUseCase.getAll(user.id!!))
    }

    override fun getRecentNotifications(
        user: User
    ): ResponseEntity<ApiResponse<List<NotificationResponse>>> {
        return ApiResponse.success(SuccessCode.REQUEST_OK, getNotificationUseCase.getRecent(user.id!!))
    }

    override fun notice(noticeRequest: NoticeRequest): ResponseEntity<ApiResponse<Unit>> {
        sendNoticeUseCase.execute(noticeRequest)
        return ApiResponse.success(SuccessCode.REQUEST_OK)
    }

    override fun sendNotification(request: SendNotificationRequest): ResponseEntity<ApiResponse<Unit>> {
        sendNotificationUseCase.execute(request)
        return ApiResponse.success(SuccessCode.REQUEST_OK)
    }
}