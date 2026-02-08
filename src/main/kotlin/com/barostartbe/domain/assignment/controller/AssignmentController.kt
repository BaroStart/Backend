package com.barostartbe.domain.assignment.controller

import com.barostartbe.domain.assignment.dto.request.AssignmentCreateReq
import com.barostartbe.domain.assignment.dto.request.AssignmentSubmitReq
import com.barostartbe.domain.assignment.dto.response.AssignmentCreateRes
import com.barostartbe.domain.assignment.dto.response.AssignmentMaterialRes
import com.barostartbe.domain.assignment.dto.response.AssignmentMenteeListRes
import com.barostartbe.domain.assignment.dto.response.AssignmentMenteeDetailRes
import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.assignment.usecase.AssignmentCommandUseCase
import com.barostartbe.domain.assignment.usecase.AssignmentFileDownloadUseCase
import com.barostartbe.domain.assignment.usecase.AssignmentMaterialQueryUseCase
import com.barostartbe.domain.assignment.usecase.AssignmentQueryUseCase
import com.barostartbe.domain.user.entity.User
import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.response.type.SuccessCode
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.LocalDate


@RestController
class AssignmentController(
    private val assignmentCommandUseCase: AssignmentCommandUseCase,
    private val assignmentQueryUseCase: AssignmentQueryUseCase,
    private val assignmentFileDownloadUseCase: AssignmentFileDownloadUseCase,
    private val assignmentMaterialQueryUseCase: AssignmentMaterialQueryUseCase
) : AssignmentApi {

    override fun createAssignment(@AuthenticationPrincipal mentor: User, @RequestBody req: AssignmentCreateReq): ResponseEntity<ApiResponse<AssignmentCreateRes>> =
        ApiResponse.success(SuccessCode.CREATE_OK, assignmentCommandUseCase.createAssignment(mentorId = mentor.id!!, menteeId = req.menteeId, req = req))

    override fun getMenteeAssignments(mentee: User, subject: Subject?, dueDate: LocalDate?): ResponseEntity<ApiResponse<List<AssignmentMenteeListRes>>> {
        val menteeId = mentee.id!!
        return ApiResponse.success(SuccessCode.REQUEST_OK, assignmentQueryUseCase.getAssignmentsByMentee(menteeId = menteeId, subject = subject, dueDate = dueDate)) }

    override fun getMenteeAssignmentDetail(@AuthenticationPrincipal mentee: User, assignmentId: Long): ResponseEntity<ApiResponse<AssignmentMenteeDetailRes>> {
        val menteeId = mentee.id!!
        return ApiResponse.success(SuccessCode.REQUEST_OK, assignmentQueryUseCase.getAssignmentDetailByMentee(assignmentId = assignmentId, menteeId = menteeId)) }

    override fun submitAssignment(@AuthenticationPrincipal mentee: User, assignmentId: Long, req: AssignmentSubmitReq): ResponseEntity<ApiResponse<Unit>> {
        assignmentCommandUseCase.submitAssignment(menteeId = mentee.id!!, assignmentId = assignmentId, req = req)
        return ApiResponse.success(SuccessCode.REQUEST_OK) }

    override fun getAssignmentFileDownloadUrl(assignmentFileId: Long): ResponseEntity<ApiResponse<String>> =
        ApiResponse.success(SuccessCode.REQUEST_OK, assignmentFileDownloadUseCase.execute(assignmentFileId))

    override fun getAllMaterials(@AuthenticationPrincipal mentor: User, subject: Subject?): ResponseEntity<ApiResponse<List<AssignmentMaterialRes>>> {
        val mentorId = mentor.id!!
        val materials = assignmentMaterialQueryUseCase.getAllMaterialsByMentor(mentorId = mentorId, subject = subject)

        return ApiResponse.success(SuccessCode.REQUEST_OK, materials)
    }
}
