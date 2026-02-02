package com.barostartbe.domain.example.error

import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode

class ExampleNotFoundException : ServiceException(ErrorCode.EXAMPLE_NOT_FOUND)
