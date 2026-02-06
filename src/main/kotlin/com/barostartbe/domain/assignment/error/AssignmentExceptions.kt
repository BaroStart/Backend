package com.barostartbe.domain.assignment.error

import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode

class AssignmentNotFoundException :                             // 조회 실패
    ServiceException(ErrorCode.ASSIGNMENT_NOT_FOUND)

class AssignmentFeedbackedException :                           // 피드백 완료 후 수정/삭제 불가
    ServiceException(ErrorCode.ASSIGNMENT_ALREADY_FEEDBACKED)

class AssignmentNotSubmittedException :                         // 제출 안 된 상태에서의 행위
    ServiceException(ErrorCode.ASSIGNMENT_NOT_SUBMITTED)

class AssignmentPermissionDeniedException :                     // 멘토/멘티 권한 문제
    ServiceException(ErrorCode.ASSIGNMENT_PERMISSION_DENIED)

