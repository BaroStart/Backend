package com.barostartbe.global.response.type

enum class SuccessCode(
    val httpStatus: Int,
    val code: String,
    val message: String,
) {
    REQUEST_OK(200, "OK", "요청이 성공적으로 처리되었습니다."),
    CREATE_OK(201, "OK", "요청이 성공적으로 처리되었습니다."),
    DELETE_OK(200, "OK", "삭제가 성공적으로 처리되었습니다.")
}
