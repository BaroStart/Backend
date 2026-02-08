package com.barostartbe.domain.feedback.repository

import com.barostartbe.domain.assignment.entity.enums.Subject
import com.barostartbe.domain.feedback.entity.Feedback
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.Optional

interface FeedbackRepository : JpaRepository<Feedback, Long> {

    // 과제 기준 피드백 단건 조회
    fun findByAssignmentId(assignmentId: Long): Feedback?

    // 과제 기준 피드백 존재 여부 조회
    fun existsByAssignmentId(assignmentId: Long): Boolean

    // 과제 ID 목록 기준 피드백 전체 조회
    fun findAllByAssignmentIdIn(assignmentIds: List<Long>): List<Feedback>

    // [멘티] 오늘 작성된 피드백 요약 목록 조회
    @Query(
        """
        select f
          from Feedback f
          join fetch f.assignment a
          join fetch a.mentor m
         where a.mentee.id = :menteeId
           and f.createdAt between :start and :end
         order by f.createdAt desc
        """
    )
    fun findDailyFeedbackSummaries(
        @Param("menteeId") menteeId: Long,
        @Param("start") start: LocalDateTime,
        @Param("end") end: LocalDateTime
    ): List<Feedback>

    // [멘티] 오늘 작성된 피드백 과목별 목록 조회
    @Query(
        """
        select f
        from Feedback f
        join f.assignment a
        where a.mentee.id = :menteeId
          and a.subject = :subject
          and f.createdAt between :start and :end
        order by f.createdAt desc
        """
    )
    fun findDailyFeedbacksByMenteeAndSubject(
        menteeId: Long,
        subject: Subject,
        start: LocalDateTime,
        end: LocalDateTime
    ): List<Feedback>

    // [멘티] 오늘 작성된 피드백 전체 목록 조회
    @Query(
        """
        select f
        from Feedback f
        join f.assignment a
        where a.mentee.id = :menteeId
          and f.createdAt between :start and :end
        order by f.createdAt desc
        """
    )
    fun findDailyFeedbacksByMentee(
        menteeId: Long,
        start: LocalDateTime,
        end: LocalDateTime
    ): List<Feedback>
}
