package com.barostartbe.domain.overall.dto.response

import com.barostartbe.domain.overall.entity.Overall
import java.time.LocalDateTime

data class OverallResponse(
    val id: Long,
    val content: String,
    val mentorName: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(overall: Overall): OverallResponse {
            return OverallResponse(
                id = overall.id!!,
                content = overall.content,
                mentorName = overall.mentor.name!!,
                createdAt = overall.createdAt!!
            )
        }
    }
}