package com.barostartbe.domain.file.usecase

import com.barostartbe.domain.objectstorage.usecase.GetPreAuthenticatedUrl
import com.barostartbe.global.annotation.QueryUseCase


@QueryUseCase
class FileQueryUseCase(
    private val getPreAuthenticatedUrl: GetPreAuthenticatedUrl
) {
    /**
     * 파일 다운로드 Pre-authenticated URL 생성
     */
    fun generateDownloadUrl(filePath: String): String {
        return getPreAuthenticatedUrl.execute(
            fileName = filePath
        ).url
    }
}
