package com.barostartbe.global.handler

import com.barostartbe.domain.errorlog.entity.ErrorLog
import com.barostartbe.domain.errorlog.repository.ErrorLogRepository
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.response.type.ErrorCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val log = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler(
    private val errorLogRepository: ErrorLogRepository
) {

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

        saveLog(errorCode)

        return ApiResponse.error(errorCode)
    }

    @ExceptionHandler(Exception::class)
    fun handleCustomException(
        e: Exception
    ): ResponseEntity<ApiResponse<Unit>> {
        val errorCode = ErrorCode.INTERNAL_SERVER_ERROR

        log.error("{}", e.message)
        e.printStackTrace()

        saveLog(errorCode)

        return ApiResponse.error(errorCode)
    }

    private fun saveLog(errorCode: ErrorCode) {
        val logEntity = ErrorLog(
            message = errorCode.message,
            status = errorCode.httpStatus
        )

        errorLogRepository.save(logEntity)
    }
}
