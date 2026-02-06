package com.barostartbe.global.response.type

enum class ErrorCode(
    val httpStatus: Int,
    val code: String,
    val message: String,
) {
    EXAMPLE_NOT_FOUND(404, "EXAMPLE_NOT_FOUND", "예제를 찾을 수 없습니다."),

    // Assignment
    ASSIGNMENT_NOT_FOUND(404, "ASSIGNMENT_NOT_FOUND", "과제를 찾을 수 없습니다."),
    ASSIGNMENT_ALREADY_FEEDBACKED(400, "ASSIGNMENT_ALREADY_FEEDBACKED", "피드백이 완료된 과제는 수정할 수 없습니다."),
    ASSIGNMENT_NOT_SUBMITTED(400, "ASSIGNMENT_NOT_SUBMITTED", "제출되지 않은 과제입니다."),
    ASSIGNMENT_PERMISSION_DENIED(403, "ASSIGNMENT_PERMISSION_DENIED", "해당 과제에 대한 권한이 없습니다."),

    // File
    FILE_NOT_FOUND(404, "FILE_NOT_FOUND", "파일을 찾을 수 없습니다."),

    // 공통
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다."),
}
