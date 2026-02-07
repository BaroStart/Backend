package com.barostartbe.domain.todo.repository

import com.barostartbe.domain.todo.entity.ToDo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface ToDoRepository : JpaRepository<ToDo, Long> {

    @Query(
        """
        SELECT t
        FROM ToDo t
        WHERE t.mentee.id = :menteeId 
            AND DATE(t.createdAt) = :date
        """
    )
    fun findAllByMenteeIdAndCreatedDate(
        @Param("menteeId") menteeId: Long,
        @Param("date") date: LocalDate
    ): List<ToDo>
}
