package com.barostartbe.domain.feedback.dto.response

import com.barostartbe.domain.assignment.entity.enums.Subject
import com.barostartbe.domain.feedback.entity.enums.FeedbackStatus
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "피드백 상세 조회 응답 DTO")
data class MentorFeedbackDetailRes(

    @Schema(description = "과제 ID")
    val assignmentId: Long,

    @Schema(description = "피드백 상태 (WAITING: 대기, DEADLINE: 마감, COMPLETED: 완료)")
    val feedbackStatus: FeedbackStatus,

    @Schema(description = "멘티 이름")
    val menteeName: String,

    @Schema(description = "멘티 학년 (예: 고3)")
    val menteeGrade: String,

    @Schema(description = "멘티 계열 (문과 / 이과)")
    val menteeTrack: String,

    @Schema(description = "과제명")
    val assignmentTitle: String,

    @Schema(description = "과목")
    val subject: Subject,

    @Schema(description = "과제 제출일시")
    val submittedAt: LocalDateTime,

    @Schema(description = "멘티가 과제 제출 시 남긴 메모", nullable = true)
    val submissionMemo: String?,

    @Schema(description = "멘티가 제출한 이미지 URL 목록 (OCI Object Storage)")
    val submissionImageUrls: List<String>
)
