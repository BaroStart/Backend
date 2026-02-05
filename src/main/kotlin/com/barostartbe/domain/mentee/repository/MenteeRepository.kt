package com.barostartbe.domain.mentee.repository

import com.barostartbe.domain.mentee.entity.Mentee
import org.springframework.data.jpa.repository.JpaRepository

interface MenteeRepository: JpaRepository<Mentee, Long> {
}