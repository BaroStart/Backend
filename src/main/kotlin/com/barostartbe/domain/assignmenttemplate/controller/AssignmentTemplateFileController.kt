package com.barostartbe.domain.assignmenttemplate.controller

import com.barostartbe.domain.assignment.entity.enum.Subject
import com.barostartbe.domain.assignmenttemplate.dto.response.AssignmentTemplateFileListRes
import com.barostartbe.domain.assignmenttemplate.usecase.AssignmentTemplateFileListQueryUseCase
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.response.type.SuccessCode
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AssignmentTemplateFileController(
    private val assignmentTemplateFileListQueryUseCase: AssignmentTemplateFileListQueryUseCase
) : AssignmentTemplateFileApi {

    override fun getTemplateFileList(
        @AuthenticationPrincipal mentor: Mentor,
        @RequestParam(required = false) subject: Subject?
    ): ResponseEntity<ApiResponse<List<AssignmentTemplateFileListRes>>> =
        ApiResponse.success(
            SuccessCode.REQUEST_OK,
            assignmentTemplateFileListQueryUseCase.execute(mentor, subject)
        )
}
