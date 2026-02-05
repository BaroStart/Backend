package com.barostartbe.domain.mentee.usecase

import com.barostartbe.domain.admin.repository.MentorMenteeMappingRepository
import com.barostartbe.domain.mentee.dto.GetMenteeInfoResponseDto
import com.barostartbe.domain.mentee.entity.Grade
import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.mentee.repository.MenteeRepository
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.domain.mentor.repository.MentorRepository
import com.barostartbe.domain.user.repository.AccessLogRepository
import com.barostartbe.global.annotation.QueryUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.repository.findByIdOrNull
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@QueryUseCase
class MenteeQueryUseCase(
    val mentorRepository: MentorRepository,
    val menteeRepository: MenteeRepository,
    val accessLogRepository: AccessLogRepository,
    val mentorMenteeMappingRepository: MentorMenteeMappingRepository,
    val redisTemplate: RedisTemplate<String, Any>
) {
    fun getMenteeInfo(mentorId: Long, menteeId: Long): GetMenteeInfoResponseDto {

        val mentor = mentorRepository.findByIdOrNull(mentorId) ?: throw ServiceException(ErrorCode.USER_NOT_FOUND)
        val mentee = menteeRepository.findByIdOrNull(menteeId) ?: throw ServiceException(ErrorCode.USER_NOT_FOUND)

        // 멘티 이름, 학년
        val menteeName = mentee.name
        val menteeGrade = mentee.grade!!.name

        // 활동중
        val isActive = if (redisTemplate.hasKey("active::user::${mentee.loginId}")) 1 else 0

        // 마지막 접속 시간
        val lastAccess = getLastAccessTime(menteeId)

        // 멘토링 시작일
        val mentoringStartDate = getMentoringStartDate(mentor, mentee)

        // 총공부시간
        // 숙제달성율
        // 평균점수

        return GetMenteeInfoResponseDto(
            menteeName = menteeName!!,
            menteeGrade = menteeGrade,
            isActive = isActive,
            lastAccess = lastAccess,
            mentoringStartDate = mentoringStartDate
        )
    }

    fun getLastAccessTime(menteeId: Long): Int {
        val accessLog = accessLogRepository.findFirstByUserIdOrderByCreatedAtDesc(menteeId)
            ?: throw ServiceException(ErrorCode.NOT_FOUND)

        return ChronoUnit.HOURS.between(accessLog.createdAt, LocalDateTime.now()).toInt()

    }

    fun getMentoringStartDate(mentor: Mentor, mentee: Mentee): String {
        val mapping = mentorMenteeMappingRepository.findByMentorAndMentee(mentor, mentee)
            ?: throw ServiceException(ErrorCode.UNMATCHED_PAIR)
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(mapping.createdAt)
    }
}