package com.barostartbe.global.error.exception

import com.barostartbe.global.response.type.ErrorCode

open class ServiceException(
    val errorCode: ErrorCode
) : RuntimeException() {
}
