package com.barostartbe.domain.assignment.controller

import com.barostartbe.domain.assignment.dto.request.AssignmentCreateReq
import com.barostartbe.domain.assignment.dto.request.AssignmentSubmitReq
import com.barostartbe.domain.assignment.dto.response.AssignmentCreateRes
import com.barostartbe.domain.assignment.usecase.AssignmentCommandUseCase
import com.barostartbe.domain.assignment.usecase.AssignmentQueryUseCase
import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.response.type.SuccessCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
class AssignmentController(
    private val assignmentCommandUseCase: AssignmentCommandUseCase,
    private val assignmentQueryUseCase: AssignmentQueryUseCase,
) : AssignmentApi {

    @PostMapping("/mentees/{menteeId}/assignments")
    override fun createAssignment(@PathVariable menteeId: Long, @RequestBody req: AssignmentCreateReq
    ): ResponseEntity<ApiResponse<AssignmentCreateRes>> =
        ApiResponse.success(
            SuccessCode.CREATE_OK,
            assignmentCommandUseCase.createAssignment(menteeId, req)
        )

    override fun submitAssignment(assignmentId: Long, req: AssignmentSubmitReq): ResponseEntity<ApiResponse<Unit>> {
        assignmentCommandUseCase.submitAssignment(req.copy(assignmentId = assignmentId))
        return ApiResponse.success(SuccessCode.REQUEST_OK)
    }

    override fun getAssignments() =
        ApiResponse.success(SuccessCode.REQUEST_OK, assignmentQueryUseCase.getAssignments())

    override fun getAssignmentDetail(assignmentId: Long) =
        ApiResponse.success(SuccessCode.REQUEST_OK, assignmentQueryUseCase.getAssignmentDetail(assignmentId))
}
