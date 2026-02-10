package com.barostartbe.domain.mentee.usecase

import com.barostartbe.domain.admin.repository.MentorMenteeMappingRepository
import com.barostartbe.domain.assignment.entity.enums.AssignmentStatus
import com.barostartbe.domain.assignment.repository.AssignmentRepository
import com.barostartbe.domain.mentee.dto.CalendarResponseDto
import com.barostartbe.domain.assignment.entity.Assignment
import com.barostartbe.domain.assignment.entity.enums.Subject
import com.barostartbe.domain.badge.repository.MenteeBadgeMappingRepository
import com.barostartbe.domain.comment.repository.CommentRepository
import com.barostartbe.domain.mentee.dto.GetMenteeCommentDashboardResponseDto
import com.barostartbe.domain.mentee.dto.GetMenteeDashboardResponseDto
import com.barostartbe.domain.mentee.dto.GetMenteeFeedbackDashboardResponseDto
import com.barostartbe.domain.mentee.dto.GetMenteeBasicInfoResponseDto
import com.barostartbe.domain.mentee.dto.GetMenteeInfoResponseDto
import com.barostartbe.domain.mentee.dto.GetMenteeMyPageResponseDto
import com.barostartbe.domain.mentee.dto.TaskInfoResponseDto
import com.barostartbe.domain.mentee.dto.GetMenteeNotCompletedAssignmentResponseDto
import com.barostartbe.domain.mentee.dto.GetMenteeTodoDashboardResponseDto
import com.barostartbe.domain.mentee.dto.GetMenteeTotalStudyTimeCalendarResponseDto
import com.barostartbe.domain.mentee.dto.GetMentoMainDashboardResponseDto
import com.barostartbe.domain.mentee.dto.GetRecentSubmittedAssignmentResponseDto
import com.barostartbe.domain.mentee.dto.GetTotalMenteeInfoResponsesDto
import com.barostartbe.domain.mentee.dto.GetWeeklyCompleteRateBySubjectResponseDto
import com.barostartbe.domain.feedback.repository.FeedbackRepository
import com.barostartbe.domain.mentee.dto.*
import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.mentee.repository.MenteeRepository
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.domain.mentor.repository.MentorRepository
import com.barostartbe.domain.todo.entity.ToDo
import com.barostartbe.domain.overall.repository.OverallRepository
import com.barostartbe.domain.todo.entity.enums.Status
import com.barostartbe.domain.todo.repository.ToDoRepository
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
    private val commentRepository: CommentRepository,
    private val feedbackRepository: FeedbackRepository,
    private val overallRepository: OverallRepository,
    private val menteeBadgeMappingRepository: MenteeBadgeMappingRepository
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

    fun getMenteeMyPage(mentee: Mentee): GetMenteeMyPageResponseDto {

        val userName = mentee.name!!

        val weeklyTotalAssignmentCount = getWeeklyTotalAssignments(mentee).count()
        val weeklyTotalCompleteAssignmentCount = getWeeklyTotalCompletedAssignments(mentee).count()
        val weeklyTotalCompleteAssignmentRate = ((weeklyTotalCompleteAssignmentCount.toFloat() / weeklyTotalAssignmentCount) / 100).toInt()

        val weeklyTotalStudyTimeTotalMinutes = getWeeklyTotalStudyTime(mentee)
        val weeklyTotalStudyTimeHour = (weeklyTotalStudyTimeTotalMinutes / 60)
        val weeklyTotalStudyTimeMinute = weeklyTotalStudyTimeTotalMinutes - 60 * weeklyTotalStudyTimeHour

        val weeklyTotalCompleteRateGroupBySubject = getWeeklyCompletedAssignmentRateBySubject(mentee)

        val totalBadgeCount = menteeBadgeMappingRepository.findAllByMentee_id(mentee.id!!).size

        return GetMenteeMyPageResponseDto(
            name = userName,
            weeklyTotalAssignmentCount = weeklyTotalAssignmentCount,
            weeklyTotalCompletedAssignmentCount = weeklyTotalCompleteAssignmentCount,
            weeklyCompleteRate = weeklyTotalCompleteAssignmentRate,
            weeklyTotalStudyTimeHour = weeklyTotalStudyTimeHour,
            weeklyTotalStudyTimeMinute = weeklyTotalStudyTimeMinute,
            weeklyCompleteRateBySubject = weeklyTotalCompleteRateGroupBySubject,
            totalBadgeCount = totalBadgeCount,
        )

    }

    fun getWeeklyCompletedAssignmentRateBySubject(mentee: Mentee) : List<GetWeeklyCompleteRateBySubjectResponseDto>{
        val weeklyTotalAssignmentCountGroupBySubject = getWeeklyTotalAssignments(mentee)
            .groupBy { it.subject }
            .map { it.key to it.value.count() }
            .toMap()

        val weeklyTotalCompletedAssignmentCountGroupBySubject = getWeeklyTotalAssignments(mentee)
            .groupBy { it.subject }
            .map { it.key to it.value.filter { it.status != AssignmentStatus.NOT_SUBMIT }.count() }
            .toMap()

        return Subject.entries.map { subject ->
            GetWeeklyCompleteRateBySubjectResponseDto(
                subject = subject.name,
                weeklyCompleteRateBySubject = if (weeklyTotalAssignmentCountGroupBySubject.containsKey(subject)){
                    weeklyTotalCompletedAssignmentCountGroupBySubject.getOrDefault(subject, 0) /
                            weeklyTotalAssignmentCountGroupBySubject[subject]!!
                }
                else 0
            )
        }
    }

    fun getWeeklyTotalStudyTime(mentee: Mentee): Int {
        val weeklyTotalStudyTimeForAssignment = getWeeklyTotalAssignments(mentee)
            .filter { it.status != AssignmentStatus.NOT_SUBMIT }
            .sumOf { it.endTime!!.toEpochSecond(ZoneOffset.UTC) - it.startTime!!.toEpochSecond(ZoneOffset.UTC) }
        val weeklyTotalStudyTimeForTodo =getWeeklyTotalDodos(mentee)
            .filter { it.status == Status.COMPLETED }
            .sumOf { it.endTime!!.toEpochSecond(ZoneOffset.UTC) - it.startTime!!.toEpochSecond(ZoneOffset.UTC) }
        return (weeklyTotalStudyTimeForTodo + weeklyTotalStudyTimeForAssignment / 60).toInt()
    }

    fun getWeeklyTotalAssignments(mentee: Mentee): List<Assignment>{
        val startDate = LocalDate.now().minusDays(7).atStartOfDay()
        val endDate = LocalDate.now().plusDays(1).atStartOfDay()
        return assignmentRepository.findAllByMenteeAndDueDateBetween(mentee, startDate, endDate)
    }

    fun getWeeklyTotalDodos(mentee: Mentee) : List<ToDo>{
        val startDate = LocalDate.now().minusDays(7).atStartOfDay()
        val endDate = LocalDate.now().plusDays(1).atStartOfDay()
        return toDoRepository.findAllByMenteeAndCreatedAtBetween(mentee, startDate, endDate)
    }

    fun getWeeklyTotalCompletedAssignments(mentee: Mentee): List<Assignment>{
        return getWeeklyTotalAssignments(mentee)
            .filter { it.status != AssignmentStatus.NOT_SUBMIT }
    }

    fun getMenteeDashboard(mentor: Mentor, menteeId: Long, searchType: String, date: String): GetMenteeDashboardResponseDto{

        val mentee = menteeRepository.findByIdOrNull(menteeId) ?: throw ServiceException(ErrorCode.USER_NOT_FOUND)
        val checkDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        return when (searchType.uppercase()){
            "DAY" -> getMenteeDashboardByDate(mentor, mentee, checkDate)
            "WEEK" -> {
                val startDate = checkDate.minusDays(8).atStartOfDay()
                val endDate = checkDate.atStartOfDay()
                getMenteeDashboardAfterStartDate(mentor, mentee, startDate, endDate)
            }
            "MONTH" -> {
                val startDate = LocalDate.of(checkDate.year, checkDate.month, 1).atStartOfDay()
                val endDate = checkDate.withDayOfMonth(checkDate.lengthOfMonth()).atStartOfDay()
                getMenteeDashboardAfterStartDate(mentor, mentee, startDate, endDate)
            }
            else -> throw ServiceException(ErrorCode.BAD_PARAMETER)
        }
    }

    fun getMenteeDashboardAfterStartDate(mentor: Mentor, mentee: Mentee, startDate: LocalDateTime, endDate: LocalDateTime): GetMenteeDashboardResponseDto{

        val feedbacks = assignmentRepository.findAllByMentorAndMenteeAndSubmittedAtBetween(mentor, mentee, startDate, endDate)
            .filter { it.status != AssignmentStatus.NOT_SUBMIT }
            .map { GetMenteeFeedbackDashboardResponseDto.from(it) }
        val notCompletedAssignments = assignmentRepository.findAllByMentorAndMenteeAndDueDateBetween(mentor, mentee, startDate, endDate)
            .filter { it.status == AssignmentStatus.NOT_SUBMIT }
            .map { GetMenteeNotCompletedAssignmentResponseDto.from(it) }
        val todoList = toDoRepository.findAllByMenteeAndCreatedAtBetween(mentee, startDate, endDate)
            .map { GetMenteeTodoDashboardResponseDto.from(it) }
        val comments = commentRepository.findAllByMenteeAndCreatedAtBetween(mentee, startDate, endDate)
            .map { GetMenteeCommentDashboardResponseDto.from(it) }

        return GetMenteeDashboardResponseDto(
            feedbacks = feedbacks,
            notCompletedAssignments = notCompletedAssignments,
            todos = todoList,
            comments = comments
        )
    }

    fun getMenteeDashboardByDate(mentor: Mentor, mentee: Mentee, checkDate: LocalDate): GetMenteeDashboardResponseDto{

        val feedbacks =  assignmentRepository.findAllByMentorIdAndMenteeIdAndSubmittedAt(mentor.id!!, mentee.id!!, checkDate)
            .filter { it.status != AssignmentStatus.NOT_SUBMIT }
            .map { GetMenteeFeedbackDashboardResponseDto.from(it) }
        val notCompletedAssignments = assignmentRepository.findAllByMentorIdAndMenteeIdAndDueDate(mentor.id, mentee.id, checkDate)
            .filter { it.status == AssignmentStatus.NOT_SUBMIT }
            .map { GetMenteeNotCompletedAssignmentResponseDto.from(it) }
        val todoList = toDoRepository.findAllByMenteeIdAndCreatedDate(mentee.id, checkDate)
            .map { GetMenteeTodoDashboardResponseDto.from(it) }
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
        val submittedAssignmentCount = totalMenteeAssignment.count { it.status != AssignmentStatus.NOT_SUBMIT }

        return ((submittedAssignmentCount.toFloat()/totalAssignmentCount) * 100).toInt()
    }

    fun getTotalStudyTime(mentee: Mentee): Int{
        val totalAssignmentTimeToSeconds = assignmentRepository.findAllByMentee_Id(mentee.id!!)
            .filter { it.status != AssignmentStatus.NOT_SUBMIT }
            .sumOf { it.endTime!!.toEpochSecond(ZoneOffset.UTC) - it.startTime!!.toEpochSecond(ZoneOffset.UTC)
        }
        val totalTodoTimeToSeconds = toDoRepository.findAllByMentee(mentee)
            .filter { it.status == Status.COMPLETED }
            .sumOf { it.endTime!!.toEpochSecond(ZoneOffset.UTC) - it.startTime!!.toEpochSecond(ZoneOffset.UTC) }

        return ((totalAssignmentTimeToSeconds + totalTodoTimeToSeconds) / (60 * 60)).toInt()
    }

    fun getTotalStudyTimeCalendar(mentee: Mentee, date: String) : List<GetMenteeTotalStudyTimeCalendarResponseDto>{
        val result = mutableMapOf<String, Int>()

        val checkDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val startDate = checkDate.atStartOfDay()
        val endDate = checkDate.withDayOfMonth(checkDate.lengthOfMonth() + 1).atStartOfDay()

        val assignmentMap: Map<String, Long> = assignmentRepository.findAllByMenteeAndDueDateBetween(mentee, startDate, endDate)
            .filter { it.status != AssignmentStatus.NOT_SUBMIT }
            .groupBy { it.startTime!!.toLocalDate().toString() }
            .map { it.key to it.value.sumOf {
                it.startTime!!.toEpochSecond(ZoneOffset.UTC) - it.endTime!!.toEpochSecond(ZoneOffset.UTC)
            } }
            .toMap()
        val todoMap = toDoRepository.findAllByMenteeAndCreatedAtBetween(mentee, startDate, endDate)
            .groupBy { it.createdAt!!.toLocalDate().toString() }
            .map { it.key to it.value.sumOf {
                it.endTime!!.toEpochSecond(ZoneOffset.UTC) - it.startTime!!.toEpochSecond(ZoneOffset.UTC)
            } }
            .toMap()

        for (day in 1 .. checkDate.dayOfMonth){
            val date = LocalDate.of(checkDate.year, checkDate.month, day).toString()
            val totalStudyHours = ((assignmentMap.getOrDefault(date, 0) + todoMap.getOrDefault(date, 0)) / (60 * 60)).toInt()
            result[date] = totalStudyHours
        }
        return result.map { GetMenteeTotalStudyTimeCalendarResponseDto(it.key, it.value) }
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

    fun getTimeTable(menteeId: Long, date: LocalDate): List<TaskInfoResponseDto> {
        val assignments = assignmentRepository.findAllByMenteeIdAndStartTimeDate(menteeId, date)
            .map { TaskInfoResponseDto.createAssignment(it) }

        val todos = toDoRepository.findAllCompletedByMenteeIdAndStartTimeDate(menteeId, date)
            .map { TaskInfoResponseDto.createToDo(it) }

        return (assignments + todos).sortedBy { it.startTime }
    }

    fun getCalendar(menteeId: Long, year: Int, month: Int): List<CalendarResponseDto> {
        val startDate = LocalDate.of(year, month, 1)
        val endDate = startDate.withDayOfMonth(startDate.lengthOfMonth())


        val assignments = assignmentRepository.findAllByMenteeIdAndDateOverlapping(menteeId, startDate, endDate)

        val todos = toDoRepository.findAllByMenteeIdAndCreatedAtBetween(menteeId, startDate, endDate)

        val todoDates = todos.mapNotNull { it.createdAt?.toLocalDate() }.toSet()

        return (0 until startDate.lengthOfMonth()).map { i ->
            val date = startDate.plusDays(i.toLong())

            val hasAssignment = assignments.any {
                val createdAt = it.createdAt?.toLocalDate()
                val dueDate = it.dueDate.toLocalDate()
                createdAt != null && !date.isBefore(createdAt) && !date.isAfter(dueDate)
            }

            val hasToDo = todoDates.contains(date)

            CalendarResponseDto(date, hasAssignment, hasToDo)
        }
    }

    fun getFeedbackCalendar(menteeId: Long, year: Int, month: Int): List<FeedbackCalendarResponseDto> {
        val startDate = LocalDate.of(year, month, 1)
        val endDate = startDate.withDayOfMonth(startDate.lengthOfMonth())

        val feedbacks = feedbackRepository.findAllByMenteeIdAndCreatedAtBetween(menteeId, startDate, endDate)
        val overalls = overallRepository.findAllByMenteeIdAndCreatedAtBetween(menteeId, startDate, endDate)

        val feedbackDates = feedbacks.map { it.createdAt!!.toLocalDate() }.toSet()
        val overallDates = overalls.map { it.createdAt!!.toLocalDate() }.toSet()

        return (0 until startDate.lengthOfMonth()).map { i ->
            val date = startDate.plusDays(i.toLong())
            val hasFeedback = feedbackDates.contains(date) || overallDates.contains(date)
            FeedbackCalendarResponseDto(date, hasFeedback)
        }
    }

    fun getMenteesTodayDetails(mentorId: Long): GetMentoMainDashboardResponseDto {

        val mentor = mentorRepository.findByIdOrNull(mentorId) ?: throw ServiceException(ErrorCode.USER_NOT_FOUND)

        val menteeInfoList: List<GetMenteeInfoResponseDto> = mentorMenteeMappingRepository.findAllByMentor(mentor)
            .map { it.mentee }
            .map {
                val menteeInfo: GetMenteeBasicInfoResponseDto = getMenteeInfo(mentor.id!!, it.id!!)
                val waitFeedbackCount: Int = getWaitFeedbackCount(mentor, it)
                val submittedAssignments: List<GetRecentSubmittedAssignmentResponseDto> = getSubmittedAssignmentsBeforeToday(mentor, it)

                val weeklyAssignments: List<Assignment> = getWeeklyAssignments(mentor, it)
                val weeklyCompletedAssignments: List<Assignment> = getWeeklyCompletedAssignments(mentor, it)
                val completeRateForWeek: Int = ((weeklyCompletedAssignments.count().toFloat() / weeklyAssignments.count()) / 100).toInt()

                GetMenteeInfoResponseDto(
                    basicInfo = menteeInfo,

                    todayAssignmentCount = getTodayTotalAssignments(mentor, it).count(),
                    todayCompletedAssignmentCount = getTodayCompletedAssignment(mentor, it).count(),

                    weeklyAssignmentCount = weeklyAssignments.count(),
                    weeklyCompletedAssignmentCount = weeklyCompletedAssignments.count(),
                    weeklyCompletedAssignmentRate = completeRateForWeek,

                    waitFeedbackCount = waitFeedbackCount,
                    recentSubmittedAssignment = submittedAssignments
                )
            }
        val menteeCount = menteeInfoList.size
        val totalWaitFeedbackCount = menteeInfoList.sumOf { it.waitFeedbackCount }
        val todayTotalAssignmentCount = menteeInfoList.sumOf { it.todayAssignmentCount }
        val todayTotalCompletedAssignmentCount = menteeInfoList.sumOf { it.todayCompletedAssignmentCount }
        val weeklyTotalCompletedAssignmentCount = menteeInfoList.sumOf { it.weeklyAssignmentCount }

        return GetMentoMainDashboardResponseDto(
            totalInfo = GetTotalMenteeInfoResponsesDto(
                totalMentees = menteeCount,
                todayTotalAssignmentCount = todayTotalAssignmentCount,
                todayTotalCompletedAssignmentCount = todayTotalCompletedAssignmentCount,
                totalWaitFeedbackCount = totalWaitFeedbackCount,
                weeklyCompleteAssignmentCount = weeklyTotalCompletedAssignmentCount
            ),
            menteeInfoList = menteeInfoList
        )
    }

    fun getSubmittedAssignmentsBeforeToday(mentor: Mentor, mentee: Mentee): List<GetRecentSubmittedAssignmentResponseDto>{
        val endDate = LocalDate.now().plusDays(1).atStartOfDay()
        return assignmentRepository.findAllByMentorAndMenteeAndDueDateBefore(mentor, mentee, endDate)
            .filter { it.status != AssignmentStatus.NOT_SUBMIT }
            .map { GetRecentSubmittedAssignmentResponseDto.from(it) }
    }

    fun getTodayCompletedAssignment(mentor: Mentor, mentee: Mentee): List<Assignment> {
        return getTodayTotalAssignments(mentor, mentee)
            .filter { it.status != AssignmentStatus.NOT_SUBMIT }
    }

    fun getTodayTotalAssignments(mentor: Mentor, mentee: Mentee): List<Assignment> {
        val startDate = LocalDate.now().atStartOfDay()
        val endDate = LocalDate.now().plusDays(1).atStartOfDay()
        return assignmentRepository.findAllByMentorAndMenteeAndDueDateBetween(mentor, mentee, startDate, endDate)
    }

    fun getWaitFeedbackCount(mentor: Mentor, mentee: Mentee): Int{
        val endDate = LocalDate.now().plusDays(1).atStartOfDay()
        return assignmentRepository.findAllByMentorAndMenteeAndDueDateBefore(mentor, mentee, endDate)
            .filter { it.status == AssignmentStatus.SUBMITTED }
            .size
    }


    fun getWeeklyCompletedAssignments(mentor: Mentor, mentee: Mentee):List<Assignment>{
        return getWeeklyAssignments(mentor, mentee)
            .filter { it.status != AssignmentStatus.NOT_SUBMIT }
    }

    fun getWeeklyAssignments(mentor: Mentor, mentee: Mentee): List<Assignment>{
        val startDate = LocalDate.now().minusDays(7).atStartOfDay()
        val endDate = LocalDate.now().plusDays(1).atStartOfDay()
        return assignmentRepository.findAllByMentorAndMenteeAndDueDateBetween(mentor, mentee, startDate, endDate)
    }
}
