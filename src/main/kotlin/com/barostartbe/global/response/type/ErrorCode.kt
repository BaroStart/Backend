package com.barostartbe.global.response.type

enum class ErrorCode(
    val httpStatus: Int,
    val code: String,
    val message: String,
) {
    BAD_PARAMETER(400, "BAD_PARAMETER", "잘못된 파라미터 전달"),

    DUPLICATED_LOGIN_ID(400, "LOGIN_ID_DUPLICATED", "로그인 아이디가 중복됩니다."),

    UNMATCHED_PAIR(400, "UNMATCHED_PAIR", "매칭되지 않은 멘토, 멘티 쌍입니다."),

    NO_AUTH(403, "NO_AUTH", "권한이 없습니다."),
  
    TODO_NOT_COMPLETED(400, "TODO_NOT_COMPLETED", "할일이 완료되지 않았습니다."),

    PASSWORD_MISMATCH(403, "PASSWORD_MISMATCH", "패스워드가 일치하지 않습니다."),

    TOKEN_EXPIRED(403, "TOKEN_EXPIRED", "토큰의 만료시간이 지났습니다."),

    INVALID_TOKEN(403, "INVALID_TOKEN", "유효한 토큰이 아닙니다."),

    EXAMPLE_NOT_FOUND(404, "EXAMPLE_NOT_FOUND", "예제를 찾을 수 없습니다."),

    // Assignment
    ASSIGNMENT_NOT_FOUND(404, "ASSIGNMENT_NOT_FOUND", "과제를 찾을 수 없습니다."),
    ASSIGNMENT_FILE_NOT_FOUND(404, "ASSIGNMENT_FILE_NOT_FOUND", "과제 파일을 찾을 수 없습니다."),
    ASSIGNMENT_ALREADY_FEEDBACKED(400, "ASSIGNMENT_ALREADY_FEEDBACKED", "피드백이 완료된 과제는 수정할 수 없습니다."),
    ASSIGNMENT_NOT_SUBMITTED(400, "ASSIGNMENT_NOT_SUBMITTED", "제출되지 않은 과제입니다."),
    ASSIGNMENT_PERMISSION_DENIED(403, "ASSIGNMENT_PERMISSION_DENIED", "해당 과제에 대한 권한이 없습니다."),

    // File
    INVALID_FILE_URL(400, "INVALID_FILE_URL", "파일 URL 형식이 올바르지 않습니다."),
    FILE_NOT_FOUND(404, "FILE_NOT_FOUND", "파일을 찾을 수 없습니다."),

    // 공통
    USER_NOT_FOUND(404, "USER_NOT_FOUND", "유저를 찾을 수 없습니다."),

    NOT_FOUND(404, "NOT_FOUND", "엔티티를 찾을 수 없습니다."),

    TODO_NOT_FOUND(404, "TODO_NOT_FOUND", "할일을 찾을 수 없습니다."),


    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다."),
}
