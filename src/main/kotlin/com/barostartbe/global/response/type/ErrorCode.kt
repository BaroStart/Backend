package com.barostartbe.global.response.type

enum class ErrorCode(
    val httpStatus: Int,
    val code: String,
    val message: String,
) {
    PASSWORD_MISMATCH(403, "PASSWORD_MISMATCH", "패스워드가 일치하지 않습니다."),

    TOKEN_EXPIRED(403, "TOKEN_EXPIRED", "토큰의 만료시간이 지났습니다."),

    INVALID_TOKEN(403, "INVALID_TOKEN", "유효한 토큰이 아닙니다."),

    EXAMPLE_NOT_FOUND(404, "EXAMPLE_NOT_FOUND", "예제를 찾을 수 없습니다."),

    USER_NOT_FOUND(404, "USER_NOT_FOUND", "유저를 찾을 수 없습니다."),

    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다."),
}
