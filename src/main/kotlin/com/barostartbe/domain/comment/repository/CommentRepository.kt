package com.barostartbe.domain.comment.repository

import com.barostartbe.domain.comment.entity.Comment
import com.barostartbe.domain.mentee.entity.Mentee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate
import java.time.LocalDateTime

interface CommentRepository: JpaRepository<Comment, Long> {
    fun findAllByMenteeIn(mentee: List<Mentee>): List<Comment>
    fun countByMentee_Id(menteeId: Long): Long

    @Query(
        """
            SELECT c FROM Comment c
            WHERE c.mentee.id = :menteeId AND DATE(c.createdAt) = :checkDate
        """
    )
    fun findAllByMenteeIdAndCreatedAt(menteeId: Long, checkDate: LocalDate): List<Comment>

    fun findAllByMenteeAndCreatedAtBetween(mentee: Mentee, startDate: LocalDateTime, endDate: LocalDateTime): List<Comment>
}
