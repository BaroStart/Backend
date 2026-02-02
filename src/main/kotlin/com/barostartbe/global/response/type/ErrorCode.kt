package com.barostartbe.global.response.type

enum class ErrorCode(
    val httpStatus: Int,
    val code: String,
    val message: String,
) {
    EXAMPLE_NOT_FOUND(404, "EXAMPLE_NOT_FOUND", "예제를 찾을 수 없습니다."),

    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다."),
}
