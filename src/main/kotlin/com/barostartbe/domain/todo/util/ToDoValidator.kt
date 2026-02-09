package com.barostartbe.domain.todo.util

import com.barostartbe.domain.assignment.repository.AssignmentRepository
import com.barostartbe.domain.todo.repository.ToDoRepository
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ToDoValidator(
    private val toDoRepository: ToDoRepository,
    private val assignmentRepository: AssignmentRepository
) {
    fun validateTimeConflict(menteeId: Long, startTime: LocalDateTime, endTime: LocalDateTime) {
        if (assignmentRepository.existsByMenteeIdAndTimeRange(
                menteeId,
                startTime,
                endTime
            )
        ) {
            throw ServiceException(ErrorCode.TODO_TIME_CONFLICT_WITH_ASSIGNMENT)
        }

        if (toDoRepository.existsByMenteeIdAndTimeRange(
                menteeId,
                startTime,
                endTime
            )
        ) {
            throw ServiceException(ErrorCode.TODO_TIME_CONFLICT_WITH_TODO)
        }
    }
}
