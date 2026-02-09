package com.barostartbe.domain.overall.repository

import com.barostartbe.domain.overall.entity.Overall
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface OverallRepository : JpaRepository<Overall, Long> {
    @Query("SELECT o FROM Overall o JOIN FETCH o.mentor WHERE o.mentee.id = :menteeId AND DATE(o.createdAt) = :date")
    fun findAllByMenteeIdAndDate(@Param("menteeId") menteeId: Long, @Param("date") date: LocalDate): List<Overall>

    @Query("SELECT o FROM Overall o JOIN FETCH o.mentor WHERE o.mentee.id = :menteeId AND o.mentor.id = :mentorId AND DATE(o.createdAt) = :date")
    fun findByMenteeIdAndMentorIdAndDate(@Param("menteeId") menteeId: Long, @Param("mentorId") mentorId: Long, @Param("date") date: LocalDate): Overall?
}
