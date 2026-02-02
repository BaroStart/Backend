package com.barostartbe.global.handler

import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.response.type.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(this.javaClass)!!

    @ExceptionHandler(ServiceException::class)
    fun handleCustomException(
        e: ServiceException
    ): ResponseEntity<ApiResponse<Unit>> {
        val errorCode: ErrorCode = e.errorCode

        log.warn(
            "Exception occurred. HTTP Status: {}, Code: {}, Message: {}",
            errorCode.httpStatus,
            errorCode.code,
            errorCode.message
        )

        return ApiResponse.error(errorCode)
    }

    @ExceptionHandler(Exception::class)
    fun handleCustomException(
        e: Exception
    ): ResponseEntity<ApiResponse<Unit>> {
        val errorCode = ErrorCode.INTERNAL_SERVER_ERROR

        log.error("{}", e.message)
        e.printStackTrace()

        return ApiResponse.error(errorCode)
    }
}
