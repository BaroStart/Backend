package com.barostartbe.domain.comment.repository

import com.barostartbe.domain.comment.entity.SubComment
import org.springframework.data.jpa.repository.JpaRepository

interface SubCommentRepository: JpaRepository<SubComment, Long> {
}