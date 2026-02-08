package com.barostartbe.domain.mentee.usecase

import com.barostartbe.domain.admin.repository.MentorMenteeMappingRepository
import com.barostartbe.domain.assignment.entity.enums.AssignmentStatus
import com.barostartbe.domain.assignment.repository.AssignmentRepository
import com.barostartbe.domain.comment.repository.CommentRepository
import com.barostartbe.domain.mentee.dto.GetMenteeCommentDashboardResponseDto
import com.barostartbe.domain.mentee.dto.GetMenteeDashboardResponseDto
import com.barostartbe.domain.mentee.dto.GetMenteeFeedbackDashboardResponseDto
import com.barostartbe.domain.mentee.dto.GetMenteeBasicInfoResponseDto
import com.barostartbe.domain.mentee.dto.GetMenteeNotCompletedAssignmentResponseDto
import com.barostartbe.domain.mentee.dto.GetMenteeTodoDashboardResponseDto
import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.mentee.repository.MenteeRepository
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.domain.mentor.repository.MentorRepository
import com.barostartbe.domain.todo.repository.ToDoRepository
import com.barostartbe.domain.todo.repository.ToDoTimeRepository
import com.barostartbe.domain.user.repository.AccessLogRepository
import com.barostartbe.global.annotation.QueryUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@QueryUseCase
class MenteeQueryUseCase(
    private val mentorRepository: MentorRepository,
    private val menteeRepository: MenteeRepository,
    private val accessLogRepository: AccessLogRepository,
    private val mentorMenteeMappingRepository: MentorMenteeMappingRepository,
    private val assignmentRepository: AssignmentRepository,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val toDoRepository: ToDoRepository,
    private val toDoTimeRepository: ToDoTimeRepository,
    private val commentRepository: CommentRepository
) {
    fun getMenteeInfo(mentorId: Long, menteeId: Long): GetMenteeBasicInfoResponseDto {

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
        val totalStudyTimeToHour = getTotalStudyTime(mentee)

        // 숙제달성율
        val assignmentCompleteRate= getAssignmentCompleteRate(mentee)

        // 평균점수

        return GetMenteeBasicInfoResponseDto(
            menteeName = menteeName!!,
            menteeGrade = menteeGrade,
            isActive = isActive,
            lastAccess = lastAccess,
            mentoringStartDate = mentoringStartDate,
            totalStudyTime = totalStudyTimeToHour,
            assignmentCompleteRate = assignmentCompleteRate,
        )
    }

    fun getMenteeDashboard(mentor: Mentor, mentee: Mentee, searchType: String, date: String?): GetMenteeDashboardResponseDto{

        return when (searchType){
            "DAY" -> getMenteeDashboardByDate(mentor, mentee, date!!)
            "WEEK" -> {
                val startDate = LocalDate.now().minusDays(7).atStartOfDay()
                getMenteeDashboardAfterStartDate(mentor, mentee, startDate)
            }
            "MONTH" -> {
                val today = LocalDate.now()
                val startDate = LocalDate.of(today.year, today.month, 1).atStartOfDay()
                getMenteeDashboardAfterStartDate(mentor, mentee, startDate)
            }
            else -> throw ServiceException(ErrorCode.BAD_PARAMETER)
        }
    }

    fun getMenteeDashboardAfterStartDate(mentor: Mentor, mentee: Mentee, startDate: LocalDateTime): GetMenteeDashboardResponseDto{

        val feedbacks = assignmentRepository.findAllByMentorAndMenteeAndSubmittedAtAfter(mentor, mentee, startDate)
            .filter { it.status != AssignmentStatus.NOT_SUBMIT }
            .map { GetMenteeFeedbackDashboardResponseDto.from(it) }
        val notCompletedAssignments = assignmentRepository.findAllByMentorAndMenteeAndDueDateAfter(mentor, mentee, startDate)
            .filter { it.status == AssignmentStatus.NOT_SUBMIT }
            .map { GetMenteeNotCompletedAssignmentResponseDto.from(it) }
        val todoList = toDoRepository.findAllByMenteeAndCreatedAtAfter(mentee, startDate)
            .flatMap { todo ->
                toDoTimeRepository.findByToDo_Id(todo.id!!)
                    .map { timeSlot ->
                        GetMenteeTodoDashboardResponseDto.of(todo, timeSlot)
                    }
            }
        val comments = commentRepository.findAllByMenteeAndCreatedAtAfter(mentee, startDate)
            .map { GetMenteeCommentDashboardResponseDto.from(it) }

        return GetMenteeDashboardResponseDto(
            feedbacks = feedbacks,
            notCompletedAssignments = notCompletedAssignments,
            todos = todoList,
            comments = comments
        )
    }

    fun getMenteeDashboardByDate(mentor: Mentor, mentee: Mentee, date: String): GetMenteeDashboardResponseDto{
        val checkDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val feedbacks =  assignmentRepository.findAllByMentorIdAndMenteeIdAndSubmittedAt(mentor.id!!, mentee.id!!, checkDate)
            .filter { it.status != AssignmentStatus.NOT_SUBMIT }
            .map { GetMenteeFeedbackDashboardResponseDto.from(it) }
        val notCompletedAssignments = assignmentRepository.findAllByMentorIdAndMenteeIdAndDueDate(mentor.id, mentee.id, checkDate)
            .filter { it.status == AssignmentStatus.NOT_SUBMIT }
            .map { GetMenteeNotCompletedAssignmentResponseDto.from(it) }
        val todoList = toDoRepository.findAllByMenteeIdAndCreatedDate(mentee.id, checkDate)
            .flatMap { todo ->
                toDoTimeRepository.findByToDo_Id(todo.id!!)
                    .map { timeSlot ->
                        GetMenteeTodoDashboardResponseDto.of(todo, timeSlot)
                    }
            }
        val comments = commentRepository.findAllByMenteeIdAndCreatedAt(mentee.id, checkDate)
            .map { GetMenteeCommentDashboardResponseDto.from(it) }

        return GetMenteeDashboardResponseDto(
            feedbacks = feedbacks,
            notCompletedAssignments = notCompletedAssignments,
            todos = todoList,
            comments = comments
        )
    }

    fun getAssignmentCompleteRate(mentee: Mentee): Int {
        val dueDate = LocalDate.now().plusDays(1).atStartOfDay()
        val totalMenteeAssignment = assignmentRepository.findAllByMenteeAndDueDateBefore(mentee, dueDate)

        val totalAssignmentCount = totalMenteeAssignment.size
        val submittedAssignmentCount = totalMenteeAssignment.count { it.status == AssignmentStatus.SUBMITTED }

        return ((submittedAssignmentCount.toFloat()/totalAssignmentCount) * 100).toInt()
    }

    fun getTotalStudyTime(mentee: Mentee): Int{
        val totalStudyTimeToSeconds = assignmentRepository.findAllByMentee_Id(mentee.id!!)
            .filter { it.status != AssignmentStatus.NOT_SUBMIT }
            .sumOf { it.endTime!!.toEpochSecond(ZoneOffset.UTC) - it.startTime!!.toEpochSecond(ZoneOffset.UTC)
        }
        return (totalStudyTimeToSeconds / (60 * 60)).toInt()
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