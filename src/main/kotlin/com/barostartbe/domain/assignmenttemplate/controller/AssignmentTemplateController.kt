package com.barostartbe.domain.assignmenttemplate.controller

import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.assignmenttemplate.dto.request.AssignmentTemplateCreateReq
import com.barostartbe.domain.assignmenttemplate.dto.request.AssignmentTemplateUpdateReq
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateDetailRes
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateListRes
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateFileRes
import com.barostartbe.domain.assignmenttemplate.usecase.AssignmentTemplateCreateUseCase
import com.barostartbe.domain.assignmenttemplate.usecase.AssignmentTemplateDeleteUseCase
import com.barostartbe.domain.assignmenttemplate.usecase.AssignmentTemplateDetailQueryUseCase
import com.barostartbe.domain.assignmenttemplate.usecase.AssignmentTemplateListQueryUseCase
import com.barostartbe.domain.assignmenttemplate.usecase.AssignmentTemplateUpdateUseCase
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.response.type.SuccessCode
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AssignmentTemplateController(
    private val assignmentTemplateCreateUseCase: AssignmentTemplateCreateUseCase,
    private val assignmentTemplateUpdateUseCase: AssignmentTemplateUpdateUseCase,
    private val assignmentTemplateDeleteUseCase: AssignmentTemplateDeleteUseCase,
    private val assignmentTemplateListQueryUseCase: AssignmentTemplateListQueryUseCase,
    private val assignmentTemplateDetailQueryUseCase: AssignmentTemplateDetailQueryUseCase
) : AssignmentTemplateApi {

    override fun createTemplate(@AuthenticationPrincipal mentor: Mentor, @RequestBody req: AssignmentTemplateCreateReq
    ): ResponseEntity<ApiResponse<AssignmentTemplateDetailRes>> =
        ApiResponse.success(
            SuccessCode.CREATE_OK,
            assignmentTemplateCreateUseCase.execute(mentor, req))

    override fun updateTemplate(
        @AuthenticationPrincipal mentor: Mentor,
        @PathVariable id: Long,
        @RequestBody req: AssignmentTemplateUpdateReq
    ): ResponseEntity<ApiResponse<AssignmentTemplateDetailRes>> = ApiResponse.success(SuccessCode.REQUEST_OK, assignmentTemplateUpdateUseCase.execute(mentor, id, req))

    override fun getTemplateList(
        @AuthenticationPrincipal mentor: Mentor,
        @RequestParam(required = false) subject: Subject?
    ): ResponseEntity<ApiResponse<List<AssignmentTemplateListRes>>> =
        ApiResponse.success(
            SuccessCode.REQUEST_OK,
            assignmentTemplateListQueryUseCase.execute(mentor, subject)
        )

    override fun getTemplateDetail(
        @AuthenticationPrincipal mentor: Mentor,
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<AssignmentTemplateDetailRes>> =
        ApiResponse.success(
            SuccessCode.REQUEST_OK,
            assignmentTemplateDetailQueryUseCase.execute(mentor, id)
        )

    override fun deleteTemplate(@AuthenticationPrincipal mentor: Mentor, @PathVariable id: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        assignmentTemplateDeleteUseCase.execute(mentor, id)
        return ApiResponse.success(SuccessCode.REQUEST_OK) }
}
