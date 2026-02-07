package com.barostartbe.domain.admin.repository

import com.barostartbe.domain.admin.entity.MentorMenteeMapping
import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.mentor.entity.Mentor
import org.springframework.data.jpa.repository.JpaRepository

interface MentorMenteeMappingRepository: JpaRepository<MentorMenteeMapping, Long> {
    fun findByMentorAndMentee(mentor: Mentor, mentee: Mentee): MentorMenteeMapping?
    fun findAllByMentor(mentor: Mentor): List<MentorMenteeMapping>
}