package com.barostartbe.domain.badge.usecase

import com.barostartbe.domain.badge.entity.Badge
import com.barostartbe.domain.badge.entity.MenteeBadgeMapping
import com.barostartbe.domain.badge.repository.BadgeRepository
import com.barostartbe.domain.badge.repository.MenteeBadgeMappingRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk

class MenteeBadgeQueryUseCaseTest : DescribeSpec({
    val menteeBadgeMappingRepository = mockk<MenteeBadgeMappingRepository>(relaxed = true)
    val badgeRepository = mockk<BadgeRepository>(relaxed = true)

    val menteeBadgeQueryUseCase = MenteeBadgeQueryUseCase(
        menteeBadgeMappingRepository,
        badgeRepository
    )

    beforeEach {
        clearMocks(menteeBadgeMappingRepository, badgeRepository)
    }

    describe("MenteeBadgeQueryUseCase") {
        val menteeId = 1L

        context("멘티의 전체 뱃지 목록 조회 시") {
            it("획득한 뱃지는 isActive가 true이고, 미획득 뱃지는 false여야 한다") {
                // given
                val badge1 = mockk<Badge> {
                    every { id } returns 1L
                    every { name } returns "첫 과제 완료"
                }
                val badge2 = mockk<Badge> {
                    every { id } returns 2L
                    every { name } returns "7일 연속 출석"
                }
                val badge3 = mockk<Badge> {
                    every { id } returns 3L
                    every { name } returns "30일 연속 출석"
                }

                val mapping1 = mockk<MenteeBadgeMapping> {
                    every { badge } returns badge1
                }

                every { badgeRepository.findAll() } returns listOf(badge1, badge2, badge3)
                every { menteeBadgeMappingRepository.findAllByMentee_id(menteeId) } returns listOf(mapping1)

                // when
                val result = menteeBadgeQueryUseCase.getAllByMentee(menteeId)

                // then
                result.size shouldBe 3
                
                val res1 = result.find { it.badgeId == 1L }!!
                res1.name shouldBe "첫 과제 완료"
                res1.isActive shouldBe true

                val res2 = result.find { it.badgeId == 2L }!!
                res2.name shouldBe "7일 연속 출석"
                res2.isActive shouldBe false

                val res3 = result.find { it.badgeId == 3L }!!
                res3.name shouldBe "30일 연속 출석"
                res3.isActive shouldBe false
            }
        }
    }
})
