package com.barostartbe.domain.assignment.entity.enum

enum class AssignmentStatus {
    NOT_SUBMIT,             // 과제 등록만 된 상태
    SUBMITTED,              // 과제 제출 완료 (피드백 대기)
    FEEDBACKED              // 멘토 피드백 완료
}