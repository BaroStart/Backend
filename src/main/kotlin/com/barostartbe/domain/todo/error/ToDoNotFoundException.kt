package com.barostartbe.domain.todo.error

import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode

class ToDoNotFoundException : ServiceException(ErrorCode.TODO_NOT_FOUND)
