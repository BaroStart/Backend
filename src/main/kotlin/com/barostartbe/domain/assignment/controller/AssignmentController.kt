package com.barostartbe.domain.assignment.controller

import com.barostartbe.domain.assignment.dto.request.AssignmentCreateReq
import com.barostartbe.domain.assignment.dto.request.AssignmentSubmitReq
import com.barostartbe.domain.assignment.dto.response.AssignmentCreateRes
import com.barostartbe.domain.assignment.dto.response.AssignmentMaterialRes
import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.assignment.usecase.AssignmentCommandUseCase
import com.barostartbe.domain.assignment.usecase.AssignmentFileDownloadUseCase
import com.barostartbe.domain.assignment.usecase.AssignmentMaterialQueryUseCase
import com.barostartbe.domain.assignment.usecase.AssignmentQueryUseCase
import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.response.type.SuccessCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
class AssignmentController(
    private val assignmentCommandUseCase: AssignmentCommandUseCase,
    private val assignmentQueryUseCase: AssignmentQueryUseCase,
    private val assignmentFileDownloadUseCase: AssignmentFileDownloadUseCase,
    private val assignmentMaterialQueryUseCase: AssignmentMaterialQueryUseCase
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

    override fun getAssignmentFileDownloadUrl(assignmentFileId: Long): ResponseEntity<ApiResponse<String>> =
        ApiResponse.success(
            SuccessCode.REQUEST_OK,
            assignmentFileDownloadUseCase.execute(assignmentFileId)
        )

    override fun getAssignments() =
        ApiResponse.success(SuccessCode.REQUEST_OK, assignmentQueryUseCase.getAssignments())

    override fun getAssignmentDetail(assignmentId: Long) =
        ApiResponse.success(SuccessCode.REQUEST_OK, assignmentQueryUseCase.getAssignmentDetail(assignmentId))

    override fun getAllMaterials(subject: Subject?): ResponseEntity<ApiResponse<List<AssignmentMaterialRes>>> {
        // TODO: SecurityContext에서 mentorId 추출
        val mentorId = 1L

        val materials = assignmentMaterialQueryUseCase.getAllMaterialsByMentor(
            mentorId = mentorId,
            subject = subject
        )

        return ApiResponse.success(SuccessCode.REQUEST_OK, materials)
    }
}
