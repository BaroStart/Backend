package com.barostartbe.domain.file.error

import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode

// 파일을 찾을 수 없을 때 발생하는 예외
class FileNotFoundException : ServiceException(ErrorCode.FILE_NOT_FOUND)