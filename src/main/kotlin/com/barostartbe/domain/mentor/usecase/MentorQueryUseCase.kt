package com.barostartbe.domain.mentor.usecase

import com.barostartbe.domain.admin.repository.MentorMenteeMappingRepository
import com.barostartbe.domain.assignment.entity.Assignment
import com.barostartbe.domain.assignment.entity.enums.AssignmentStatus
import com.barostartbe.domain.assignment.repository.AssignmentRepository
import com.barostartbe.domain.mentee.dto.GetMenteeBasicInfoResponseDto
import com.barostartbe.domain.mentee.dto.GetMenteeInfoResponseDto
import com.barostartbe.domain.mentee.dto.GetMentoMainDashboardResponseDto
import com.barostartbe.domain.mentee.dto.GetRecentSubmittedAssignmentResponseDto
import com.barostartbe.domain.mentee.dto.GetTotalMenteeInfoResponsesDto
import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.mentee.usecase.MenteeQueryUseCase
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.global.annotation.QueryUseCase
import org.springframework.expression.spel.ast.Assign
import java.time.LocalDate

@QueryUseCase
 class MentorQueryUseCase(
     private val mentorMenteeMappingRepository: MentorMenteeMappingRepository,
     private val menteeQueryUseCase: MenteeQueryUseCase,
     private val assignmentRepository: AssignmentRepository
 ) {
    fun getMenteesTodayDetails(mentor: Mentor): GetMentoMainDashboardResponseDto {
        val menteeInfoList: List<GetMenteeInfoResponseDto> = mentorMenteeMappingRepository.findAllByMentor(mentor)
            .map { it.mentee }
            .map {
                val menteeInfo: GetMenteeBasicInfoResponseDto = menteeQueryUseCase.getMenteeInfo(mentor.id!!, it.id!!)
                val completeRateForWeek: Int = getWeeklyCompletedAssignmentRate(mentor, it)
                val waitFeedbackCount: Int = getWaitFeedbackCount(mentor, it)
                val submittedAssignments: List<GetRecentSubmittedAssignmentResponseDto> = getSubmittedAssignmentsBeforeToday(mentor, it)

                val weeklyAssignments: List<Assignment> = getWeeklyAssignments(mentor, it)
                val weeklyCompletedAssignments: List<Assignment> = getWeeklyCompletedAssignments(weeklyAssignments)

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

    fun getWeeklyCompletedAssignmentRate(mentor: Mentor, mentee: Mentee): Int{
        val assignments = getWeeklyAssignments(mentor, mentee)
        val completeAssignmentsCountForWeek = getWeeklyCompletedAssignments(assignments)
        return ((completeAssignmentsCountForWeek.count().toFloat() / assignments.size) * 100).toInt()
    }

    fun getWeeklyCompletedAssignments(assignments: List<Assignment>):List<Assignment>{
        return assignments.filter { it.status != AssignmentStatus.NOT_SUBMIT }
    }

    fun getWeeklyAssignments(mentor: Mentor, mentee: Mentee): List<Assignment>{
        val startDate = LocalDate.now().minusDays(7).atStartOfDay()
        val endDate = LocalDate.now().plusDays(1).atStartOfDay()
        return assignmentRepository.findAllByMentorAndMenteeAndDueDateBetween(mentor, mentee, startDate, endDate)
    }
}