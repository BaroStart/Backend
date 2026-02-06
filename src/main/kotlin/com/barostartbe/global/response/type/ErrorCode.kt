package com.barostartbe.global.response.type

enum class ErrorCode(
    val httpStatus: Int,
    val code: String,
    val message: String,
) {
    BAD_PARAMETER(400, "BAD_PARAMETER", "잘못된 파라미터 전달"),

    DUPLICATED_LOGIN_ID(400, "LOGIN_ID_DUPLICATED", "로그인 아이디가 중복됩니다."),

    UNMATCHED_PAIR(400, "UNMATCHED_PAIR", "매칭되지 않은 멘토, 멘티 쌍입니다."),

    TODO_NOT_COMPLETED(400, "TODO_NOT_COMPLETED", "할일이 완료되지 않았습니다."),

    PASSWORD_MISMATCH(403, "PASSWORD_MISMATCH", "패스워드가 일치하지 않습니다."),

    TOKEN_EXPIRED(403, "TOKEN_EXPIRED", "토큰의 만료시간이 지났습니다."),

    INVALID_TOKEN(403, "INVALID_TOKEN", "유효한 토큰이 아닙니다."),

    EXAMPLE_NOT_FOUND(404, "EXAMPLE_NOT_FOUND", "예제를 찾을 수 없습니다."),

    USER_NOT_FOUND(404, "USER_NOT_FOUND", "유저를 찾을 수 없습니다."),

    NOT_FOUND(404, "NOT_FOUND", "엔티티를 찾을 수 없습니다."),

    TODO_NOT_FOUND(404, "TODO_NOT_FOUND", "할일을 찾을 수 없습니다."),


    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다."),
}
