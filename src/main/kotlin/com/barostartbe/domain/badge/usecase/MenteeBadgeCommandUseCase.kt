package com.barostartbe.domain.badge.usecase

import com.barostartbe.domain.assignment.usecase.AssignmentQueryUseCase
import com.barostartbe.domain.badge.entity.MenteeBadgeMapping
import com.barostartbe.domain.badge.repository.BadgeRepository
import com.barostartbe.domain.badge.repository.MenteeBadgeMappingRepository
import com.barostartbe.domain.comment.usecase.CommentQueryUseCase
import com.barostartbe.domain.mentee.repository.MenteeRepository
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
    private val commentQueryUseCase: CommentQueryUseCase
) {
    fun updateBadgeForMentee(menteeId: Long) {
        val mentee = menteeRepository.findByIdOrNull(menteeId) ?: throw ServiceException(ErrorCode.MENTEE_NOT_FOUND)
        val acquiredBadgeIds = menteeBadgeMappingRepository.findAllByMentee_id(menteeId)
            .mapNotNull { it.badge.id }
            .toSet()

        val badgeList = badgeRepository.findAll()

        for (badge in badgeList) {
            if (badge.id in acquiredBadgeIds) {
                continue
            }

            when (badge.name) {
                "첫 과제 완료" -> {
                    if (assignmentQueryUseCase.checkCompletedAssignmentExists(menteeId)) {
                        menteeBadgeMappingRepository.save(
                            MenteeBadgeMapping(mentee, badge)
                        )
                    }
                }

                "7일 연속 출석" -> {
                    if (accessLogQueryUseCase.getMaxConsecutiveDays(menteeId) >= 7) {
                        menteeBadgeMappingRepository.save(
                            MenteeBadgeMapping(mentee, badge)
                        )
                    }
                }

                "30일 연속 출석" -> {
                    if (accessLogQueryUseCase.getMaxConsecutiveDays(menteeId) >= 30) {
                        menteeBadgeMappingRepository.save(
                            MenteeBadgeMapping(mentee, badge)
                        )
                    }
                }

                "주간목표 달성" -> {
                    if (assignmentQueryUseCase.is7DaysAssignmentCompletedStreak(menteeId)) {
                        menteeBadgeMappingRepository.save(
                            MenteeBadgeMapping(mentee, badge)
                        )
                    }
                }

                "오늘도 한 걸음" -> {
                    if (toDoQueryUseCase.is7DaysToDoCompletedStreak(menteeId)) {
                        menteeBadgeMappingRepository.save(
                            MenteeBadgeMapping(mentee, badge)
                        )
                    }
                }

                "질문왕" -> {
                    if (commentQueryUseCase.hasMoreThanTenComments(menteeId)) {
                        menteeBadgeMappingRepository.save(
                            MenteeBadgeMapping(mentee, badge)
                        )
                    }
                }

                "국어 마스터" -> {
                    // TODO: 구현
                }

                "수학 마스터" -> {
                    // TODO: 구현
                }

                "영어 마스터" -> {
                    // TODO: 구현
                }

                "100시간 학습" -> {
                    // TODO: 구현
                }

                "포모도로 마스터" -> {
                    // TODO: 구현
                    val assignmentCount = 0
                    val toDoCount = toDoQueryUseCase.getCompletedOver25MinutesCount(menteeId)

                    if (assignmentCount + toDoCount >= 20) {
                        menteeBadgeMappingRepository.save(
                            MenteeBadgeMapping(mentee, badge)
                        )
                    }
                }

                "아침 루틴" -> {
                    // TODO: 구현
                    val assignmentCount = 0
                    val toDoCount = toDoQueryUseCase.getStudyBetweenSixAndNineCount(menteeId)

                    if (assignmentCount + toDoCount >= 7) {
                        menteeBadgeMappingRepository.save(
                            MenteeBadgeMapping(mentee, badge)
                        )
                    }
                }
            }
        }
    }
}
