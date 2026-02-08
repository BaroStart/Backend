package com.barostartbe.domain.badge.usecase

import com.barostartbe.domain.assignment.usecase.AssignmentQueryUseCase
import com.barostartbe.domain.badge.entity.Badge
import com.barostartbe.domain.badge.entity.MenteeBadgeMapping
import com.barostartbe.domain.badge.repository.BadgeRepository
import com.barostartbe.domain.badge.repository.MenteeBadgeMappingRepository
import com.barostartbe.domain.comment.usecase.CommentQueryUseCase
import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.mentee.repository.MenteeRepository
import com.barostartbe.domain.notification.dto.request.SendNotificationRequest
import com.barostartbe.domain.notification.entity.enums.Type
import com.barostartbe.domain.notification.usecase.SendNotificationUseCase
import com.barostartbe.domain.todo.usecase.ToDoQueryUseCase
import com.barostartbe.domain.user.usecase.AccessLogQueryUseCase
import com.barostartbe.global.annotation.CommandUseCase
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import org.springframework.data.repository.findByIdOrNull

@CommandUseCase
class MenteeBadgeCommandUseCase(
    private val menteeBadgeMappingRepository: MenteeBadgeMappingRepository,
    private val badgeRepository: BadgeRepository,
    private val menteeRepository: MenteeRepository,

    private val assignmentQueryUseCase: AssignmentQueryUseCase,
    private val accessLogQueryUseCase: AccessLogQueryUseCase,
    private val toDoQueryUseCase: ToDoQueryUseCase,
    private val commentQueryUseCase: CommentQueryUseCase,
    private val sendNotificationUseCase: SendNotificationUseCase
) {
    fun updateBadgeForMentee(menteeId: Long) {
        val mentee = menteeRepository.findByIdOrNull(menteeId)
            ?: throw ServiceException(ErrorCode.MENTEE_NOT_FOUND)

        val acquiredBadgeIds = menteeBadgeMappingRepository.findAllByMentee_id(menteeId)
            .mapNotNull { it.badge.id }
            .toSet()

        val allBadges = badgeRepository.findAll()

        allBadges.asSequence()
            .filterNot { it.id in acquiredBadgeIds } // 이미 획득한 뱃지 제외
            .filter { checkBadgeCondition(it.name, menteeId) } // 획득 조건 만족 여부 확인
            .forEach { badge -> grantBadge(mentee, badge) } // 뱃지 부여 및 알림
    }

    private fun checkBadgeCondition(badgeName: String, menteeId: Long): Boolean {
        return when (badgeName) {
            "첫 과제 완료" -> assignmentQueryUseCase.checkCompletedAssignmentExists(menteeId)
            "7일 연속 출석" -> accessLogQueryUseCase.getCurrentConsecutiveDays(menteeId) >= 7
            "30일 연속 출석" -> accessLogQueryUseCase.getCurrentConsecutiveDays(menteeId) >= 30
            "주간목표 달성" -> assignmentQueryUseCase.is7DaysAssignmentCompletedStreak(menteeId)
            "오늘도 한 걸음" -> toDoQueryUseCase.is7DaysToDoCompletedStreak(menteeId)
            "질문왕" -> commentQueryUseCase.hasMoreThanTenComments(menteeId)
            // "국어 마스터" ->
            // "수학 마스터" ->
            // "영어 마스터" ->
            // "100시간 학습" ->
            "포모도로 마스터" -> {
                // TODO: 구현 (AssignmentCount 로직 추가 필요)
                val assignmentCount = 0
                val toDoCount = toDoQueryUseCase.getCompletedOver25MinutesCount(menteeId)
                (assignmentCount + toDoCount) >= 20
            }
            "아침 루틴" -> {
                // TODO: 구현 (AssignmentCount 로직 추가 필요)
                val assignmentCount = 0
                val toDoCount = toDoQueryUseCase.getStudyBetweenSixAndNineCount(menteeId)
                (assignmentCount + toDoCount) >= 7
            }
            // TODO: 국어, 수학, 영어, 100시간 학습 등 미구현 뱃지는 false 처리
            else -> false
        }
    }

    private fun grantBadge(mentee: Mentee, badge: Badge) {
        menteeBadgeMappingRepository.save(MenteeBadgeMapping(mentee, badge))

        sendNotificationUseCase.execute(
            SendNotificationRequest(
                receiverId = mentee.id!!,
                title = Type.NEW_BADGE.titleFormat,
                message = Type.NEW_BADGE.messageFormat.format(badge.name)
            )
        )
    }
}
