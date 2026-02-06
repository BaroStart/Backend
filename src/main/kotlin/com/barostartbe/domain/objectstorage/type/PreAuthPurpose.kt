package com.barostartbe.domain.objectstorage.type

/**
 * Pre-Authenticated URL 목적
 * - UPLOAD   : ObjectWrite
 * - DOWNLOAD : ObjectRead
 */
enum class PreAuthPurpose {
    UPLOAD,
    DOWNLOAD
}