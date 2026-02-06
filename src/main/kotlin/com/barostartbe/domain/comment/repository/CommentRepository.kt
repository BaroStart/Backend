package com.barostartbe.domain.comment.repository

import com.barostartbe.domain.comment.entity.Comment
import com.barostartbe.domain.mentee.entity.Mentee
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository: JpaRepository<Comment, Long> {
    fun findAllByMenteeIn(mentee: List<Mentee>): List<Comment>
}