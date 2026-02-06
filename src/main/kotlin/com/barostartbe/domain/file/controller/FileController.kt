package com.barostartbe.domain.file.controller

import com.barostartbe.domain.file.dto.request.FileUploadReq
import com.barostartbe.domain.file.dto.response.FileDownloadUrlRes
import com.barostartbe.domain.file.dto.response.FileRes
import com.barostartbe.domain.file.usecase.FileCommandUseCase
import com.barostartbe.domain.file.usecase.FileQueryUseCase
import com.barostartbe.domain.objectstorage.type.PreAuthPurpose
import com.barostartbe.domain.objectstorage.usecase.GetPreAuthenticatedUrl
import com.barostartbe.global.response.ApiResponse
import com.barostartbe.global.response.type.SuccessCode
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class FileController(
    private val fileQueryUseCase: FileQueryUseCase,
    private val fileCommandUseCase: FileCommandUseCase,
    private val getPreAuthenticatedUrl: GetPreAuthenticatedUrl
) : FileApi {

    /**
     * 파일 업로드 (메타데이터 저장)
     */
    override fun uploadFile(
        @Valid @RequestBody request: FileUploadReq
    ): ResponseEntity<ApiResponse<FileRes>> {
        // TODO Security: 현재 사용자 정보 추출 및 권한 검증
        val file = fileCommandUseCase.createFile(
            fileName = request.fileName,
            filePath = request.filePath
        )
        return ApiResponse.success(
            SuccessCode.CREATE_OK,
            FileRes.from(file)
        )
    }

    /**
     * 파일 정보 조회
     * TODO Security
     */
    override fun getFileById(
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<FileRes>> {
        // TODO Security: 파일 접근 권한 검증
        val file = fileQueryUseCase.findById(id)
        return ApiResponse.success(
            SuccessCode.REQUEST_OK,
            FileRes.from(file)
        )
    }

    /**
     * 파일 다운로드 URL 발급
     * - OCI Object Storage의 Pre-Authenticated URL을 발급하여 반환
     * - URL 유효시간: 15분
     */
    override fun getDownloadUrl(
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<FileDownloadUrlRes>> {
        // TODO Security 파일 다운로드 권한 검증
        val file = fileQueryUseCase.findById(id)

        // objectKey(filePath)로 Pre-Authenticated URL 발급
        val preAuthUrlResponse = getPreAuthenticatedUrl.execute(
            fileName = file.filePath!!,
            purpose = PreAuthPurpose.DOWNLOAD
        )

        val response = FileDownloadUrlRes(
            fileId = file.id!!,
            fileName = file.fileName!!,
            downloadUrl = preAuthUrlResponse.url
        )

        return ApiResponse.success(
            SuccessCode.REQUEST_OK,
            response
        )
    }

    /**
     * 파일 삭제
     */
    override fun deleteFile(
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        val file = fileQueryUseCase.findById(id)
        fileCommandUseCase.delete(file)

        return ApiResponse.success(
            SuccessCode.DELETE_OK,
            Unit
        )
    }

    /**
     * 전체 파일 목록 조회
     */
    override fun getAllFiles(): ResponseEntity<ApiResponse<List<FileRes>>> {
        val files = fileQueryUseCase.findAll()
        val response = files.map { FileRes.from(it) }

        return ApiResponse.success(
            SuccessCode.REQUEST_OK,
            response
        )
    }
}