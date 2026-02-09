package com.barostartbe.domain.notification.controller

import com.barostartbe.domain.notification.dto.request.NoticeRequest
import com.barostartbe.domain.notification.dto.request.SendNotificationRequest
import com.barostartbe.domain.notification.dto.response.NotificationResponse
import com.barostartbe.domain.user.entity.User
import com.barostartbe.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/notification")
@Tag(name = "Notification API", description = "알림 관리 API")
interface NotificationApi {

    @PatchMapping("/read/{notification-id}")
    @Operation(summary = "알림 읽음", description = "알림을 읽음 처리 합니다.")
    fun readNotification(
        @AuthenticationPrincipal user: User,
        @Parameter(description = "notification Id", example = "1")
        @PathVariable(name = "notification-id") notificationId: Long
    ): ResponseEntity<ApiResponse<Unit>>

    @GetMapping
    @Operation(summary = "전체 알림 목록 조회", description = "전체 알림을 목록으로 조회합니다.")
    fun getNotifications(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<List<NotificationResponse>>>

    @GetMapping("/recent")
    @Operation(summary = "최근 알림 목록 조회", description = "최근 3개의 알림을 목록으로 조회합니다.")
    fun getRecentNotifications(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<List<NotificationResponse>>>

    @PostMapping("/notice")
    @Operation(summary = "공지 발송(관리자)", description = "모든 사용자에게 공지를 발송합니다.")
    fun notice(@RequestBody noticeRequest: NoticeRequest): ResponseEntity<ApiResponse<Unit>>

    @PostMapping("/send")
    @Operation(summary = "개별 알림 발송(관리자)", description = "특정 사용자에게 알림을 발송합니다.")
    fun sendNotification(@RequestBody request: SendNotificationRequest): ResponseEntity<ApiResponse<Unit>>
}
