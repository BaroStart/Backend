package com.barostartbe.global.response

import com.barostartbe.global.response.type.ErrorCode
import com.barostartbe.global.response.type.SuccessCode
import org.springframework.http.ResponseEntity

data class ApiResponse<T>(
    val status: Int,
    val code: String,
    val message: String,
    val result: T? = null,
) {
    companion object {
        fun success(successCode: SuccessCode): ResponseEntity<ApiResponse<Unit>> =
            ResponseEntity
                .status(successCode.httpStatus)
                .body(
                    ApiResponse(
                        successCode.httpStatus,
                        successCode.code,
                        successCode.message
                    )
                )

        fun <T> success(successCode: SuccessCode, result: T): ResponseEntity<ApiResponse<T>> =
            ResponseEntity
                .status(successCode.httpStatus)
                .body(
                    ApiResponse(
                        successCode.httpStatus,
                        successCode.code,
                        successCode.message,
                        result
                    )
                )

        fun error(errorCode: ErrorCode): ResponseEntity<ApiResponse<Unit>> =
            ResponseEntity
                .status(errorCode.httpStatus)
                .body(
                    ApiResponse(
                        errorCode.httpStatus,
                        errorCode.code,
                        errorCode.message
                    )
                )
    }
}
