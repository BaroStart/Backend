package com.barostartbe.domain.notification.entity.enums

enum class Type(
    val description: String
) {
    // mentor
    ASSIGNMENT_SUBMITTED("과제 제출"),
    FEEDBACK_REQUIRED("피드백 요청"),

    // mentee
    FEEDBACK_RECEIVED("피드백 수신"),
    UNSUBMIT_ASSIGNMENT("미제출 과제"),
    DEADLINE_ASSIGNMENT("과제 마감 임박"),
    NEW_BADGE("새 뱃지 획득"),
    NEW_COMMENT("새로운 코멘트"),

    // common
    SYSTEM("시스템"),
    ETC("기타")
}
