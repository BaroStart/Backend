package com.barostartbe.domain.comment.controller

import com.barostartbe.domain.comment.dto.CreateCommentRequestDto
import com.barostartbe.domain.comment.dto.CreateCommentResponseDto
import com.barostartbe.domain.comment.dto.CreateSubCommentRequestDto
import com.barostartbe.domain.comment.dto.CreateSubCommentResponseDto
import com.barostartbe.domain.comment.dto.GetCommentResponseDto
import com.barostartbe.domain.comment.dto.GetSubCommentResponseDto
import com.barostartbe.domain.comment.dto.UpdateCommentRequestDto
import com.barostartbe.domain.comment.dto.UpdateSubCommentRequestDto
import com.barostartbe.domain.user.entity.User
import com.barostartbe.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "Comment API", description = "오늘의 한마디, 질문")
@RequestMapping( "/api/v1")
interface CommentApi {

    @PostMapping("/comments")
    @Operation(summary = "코멘트 생성")
    fun createComment(@RequestBody request: CreateCommentRequestDto, @AuthenticationPrincipal user: User): ResponseEntity<ApiResponse<CreateCommentResponseDto>>

    @PatchMapping("/comments/{commentId}")
    @Operation(summary = "코멘트 수정")
    fun updateComment(@PathVariable commentId: Long, @RequestBody request: UpdateCommentRequestDto): ResponseEntity<ApiResponse<Unit>>

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "코멘트 삭제")
    fun deleteComment(@PathVariable commentId: Long): ResponseEntity<ApiResponse<Unit>>

    @GetMapping("/comments")
    @Operation(summary = "코멘트 조회")
    fun getComment(@RequestParam(value = "mentor-id") mentorId: Long) : ResponseEntity<ApiResponse<List<GetCommentResponseDto>>>

    @PostMapping("/sub-comments")
    @Operation(summary = "댓글 작성")
    fun createSubComment(@RequestBody request: CreateSubCommentRequestDto): ResponseEntity<ApiResponse<CreateSubCommentResponseDto>>

    @PatchMapping("/sub-comments/{subCommentId}")
    @Operation(summary = "댓글 수정")
    fun updateSubComment(@PathVariable subCommentId: Long, @RequestBody request: UpdateSubCommentRequestDto): ResponseEntity<ApiResponse<Unit>>

    @DeleteMapping("/sub-comments/{subCommentId}")
    @Operation(summary = "댓글 삭제")
    fun deleteSubComment(@PathVariable subCommentId: Long): ResponseEntity<ApiResponse<Unit>>

    @GetMapping("/sub-comments")
    @Operation(summary = "댓글 조회")
    fun getSubComments(@RequestParam commentId: Long) : ResponseEntity<ApiResponse<List<GetSubCommentResponseDto>>>

}