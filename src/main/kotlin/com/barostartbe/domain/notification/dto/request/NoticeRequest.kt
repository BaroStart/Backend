package com.barostartbe.domain.notification.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "공지사항 요청 DTO")
data class NoticeRequest(
    @Schema(description = "제목", example = "공지사항 제목")
    @field:NotBlank(message = "제목은 필수 입력값입니다")
    @field:Size(max = 100, message = "제목은 100자 이내로 입력해주세요")
    val title: String,

    @Schema(description = "내용", example = "공지사항 내용")
    @field:NotBlank(message = "내용은 필수 입력값입니다")
    @field:Size(max = 1000, message = "내용은 1000자 이내로 입력해주세요")
    val content: String
)
