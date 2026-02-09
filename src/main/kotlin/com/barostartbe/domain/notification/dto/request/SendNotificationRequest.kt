package com.barostartbe.domain.notification.dto.request

import com.barostartbe.domain.notification.entity.enums.Type
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "개별 알림 전송 요청 DTO")
data class SendNotificationRequest(
    @Schema(description = "수신자 ID", example = "1")
    @NotBlank(message = "수신자 ID는 필수입니다")
    val receiverId: Long,

    @Schema(description = "알림 제목", example = "개별 알림 제목")
    @NotBlank(message = "제목은 필수입니다")
    val title: String,

    @Schema(description = "알림 메시지", example = "개별 알림 내용입니다.")
    @NotBlank(message = "메시지는 필수입니다")
    val message: String,

    @Schema(description = "알림 타입 (기본 ETC)", example = "ETC")
    val type: Type?
)
