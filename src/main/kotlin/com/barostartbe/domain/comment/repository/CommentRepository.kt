package com.barostartbe.domain.comment.repository

import com.barostartbe.domain.comment.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository: JpaRepository<Comment, Long> {
}