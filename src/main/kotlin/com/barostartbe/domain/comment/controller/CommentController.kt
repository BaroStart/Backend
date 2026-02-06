package com.barostartbe.domain.comment.controller

import com.barostartbe.domain.comment.dto.CreateCommentRequestDto
import com.barostartbe.domain.comment.dto.CreateCommentResponseDto
import com.barostartbe.domain.comment.dto.CreateSubCommentRequestDto
import com.barostartbe.domain.comment.dto.CreateSubCommentResponseDto
import com.barostartbe.domain.comment.dto.GetCommentResponseDto
import com.barostartbe.domain.comment.dto.GetSubCommentResponseDto
import com.barostartbe.domain.comment.dto.UpdateCommentRequestDto
import com.barostartbe.domain.comment.dto.UpdateSubCommentRequestDto
import com.barostartbe.domain.comment.usecase.CommentCommandUseCase
import com.barostartbe.domain.comment.usecase.CommentQueryUseCase
import com.barostartbe.domain.user.entity.User
import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.response.type.SuccessCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class CommentController(
    private val commentCommandUseCase: CommentCommandUseCase,
    private val commentQueryUseCase: CommentQueryUseCase
): CommentApi {

    override fun createComment(request: CreateCommentRequestDto, user: User): ResponseEntity<ApiResponse<CreateCommentResponseDto>>
        = ApiResponse.success(SuccessCode.CREATE_OK, commentCommandUseCase.createComment(user, request))

    override fun updateComment(commentId: Long, request: UpdateCommentRequestDto): ResponseEntity<ApiResponse<Unit>>{
        commentCommandUseCase.updateComment(commentId, request)
        return ApiResponse.success(SuccessCode.REQUEST_OK)
    }

    override fun deleteComment(commentId: Long): ResponseEntity<ApiResponse<Unit>> {
        commentCommandUseCase.deleteComment(commentId)
        return ApiResponse.success(SuccessCode.REQUEST_OK)
    }

    override fun getComment(mentorId: Long): ResponseEntity<ApiResponse<List<GetCommentResponseDto>>>
        = ApiResponse.success(SuccessCode.REQUEST_OK, commentQueryUseCase.getAllCommentsForMentor(mentorId))

    override fun createSubComment(request: CreateSubCommentRequestDto): ResponseEntity<ApiResponse<CreateSubCommentResponseDto>>
        = ApiResponse.success(SuccessCode.CREATE_OK, commentCommandUseCase.createSubComment(request))

    override fun updateSubComment(subCommentId: Long, request: UpdateSubCommentRequestDto): ResponseEntity<ApiResponse<Unit>> {
        commentCommandUseCase.updateSubComment(subCommentId, request)
        return ApiResponse.success(SuccessCode.REQUEST_OK)
    }

    override fun deleteSubComment(subCommentId: Long): ResponseEntity<ApiResponse<Unit>> {
        commentCommandUseCase.deleteSubComment(subCommentId)
        return ApiResponse.success(SuccessCode.REQUEST_OK)
    }

    override fun getSubComments(commentId: Long): ResponseEntity<ApiResponse<List<GetSubCommentResponseDto>>>
        = ApiResponse.success(SuccessCode.REQUEST_OK, commentQueryUseCase.getAllSubComments(commentId))
}