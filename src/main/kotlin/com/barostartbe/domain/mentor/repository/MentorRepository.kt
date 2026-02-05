package com.barostartbe.domain.mentor.repository

import com.barostartbe.domain.mentor.entity.Mentor
import org.springframework.data.jpa.repository.JpaRepository

interface MentorRepository: JpaRepository<Mentor, Long> {
}