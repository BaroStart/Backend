package com.barostartbe.domain.notification.entity.enums

enum class Type(
    val description: String,
    val titleFormat: String,
    val messageFormat: String
) {
    // mentor
    ASSIGNMENT_SUBMITTED("과제 제출", "과제 제출 알림", "%s 멘티님께서, [%s - %s] 과제를 제출하였습니다."),
    FEEDBACK_REQUIRED("피드백 마감시간 초과", "피드백 마감시간 초과", "%s 멘티님의, [%s - %s] 과제에 대한 피드백 작성이 마감시간을 초과했습니다."),

    // mentee
    FEEDBACK_RECEIVED("피드백 수신", "새 피드백 도착", "%s 멘토님께서, [%s - %s] 과제에 피드백을 남겼습니다."),
    NEW_ASSIGNMENT("새 과제 배정", "새 과제 도착", "%s 멘토님께서, [%s - %s] 과제를 새로 배정했습니다."),
    UNSUBMIT_ASSIGNMENT("과제 미완료", "미완료 과제 알림", "[%s - %s] 과제의 마감시간이 지났습니다."),
    DEADLINE_ASSIGNMENT("과제 마감 임박", "과제 마감 임박!", "%s 과제의 마감 기한까지 %s분 남았습니다."),
    NEW_BADGE("새 뱃지 획득", "새 뱃지 획득!", "%s 뱃지를 획득하였습니다."),
    OVERALL("플래너 총평", "플래너 총평 도착", "%s 멘토님께서 플래너 총평을 남겼습니다."),

    // common
    NEW_COMMENT("새로운 코멘트", "새로운 코멘트 도착", "%s님이 새로운 코멘트를 남겼습니다."),
    NOTICE("공지사항","%s", "%s"),
    ETC("기타", "%s", "%s")
}
