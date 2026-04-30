package com.barostartbe.bench

import com.barostartbe.domain.assignment.entity.Assignment
import com.barostartbe.domain.assignment.entity.enums.AssignmentStatus
import com.barostartbe.domain.assignment.entity.enums.Subject
import com.barostartbe.domain.assignment.repository.AssignmentRepository
import com.barostartbe.domain.feedback.dto.request.FeedbackCreateReq
import com.barostartbe.domain.feedback.repository.FeedbackRepository
import com.barostartbe.domain.feedback.usecase.FeedbackCreateUseCase
import com.barostartbe.domain.mentee.entity.Grade
import com.barostartbe.domain.mentee.entity.Mentee
import com.barostartbe.domain.mentee.entity.School
import com.barostartbe.domain.mentee.repository.MenteeRepository
import com.barostartbe.domain.mentor.entity.Mentor
import com.barostartbe.domain.mentor.repository.MentorRepository
import com.barostartbe.domain.notification.repository.NotificationRepository
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

// 피드백 생성에서 race 차단 / 비즈니스 로직 차단 동작을 두 시나리오로 측정
@SpringBootTest
@ActiveProfiles("bench")
@Import(BenchTestConfig::class)
class FeedbackConcurrencyBench @Autowired constructor(
    private val feedbackCreateUseCase: FeedbackCreateUseCase,
    private val mentorRepository: MentorRepository,
    private val menteeRepository: MenteeRepository,
    private val assignmentRepository: AssignmentRepository,
    private val feedbackRepository: FeedbackRepository,
    private val notificationRepository: NotificationRepository,
) {

    private val totalRequests = 100

    private fun freshAssignmentFixture(suffix: String): Triple<Long, Long, FeedbackCreateReq> {
        val mentor = mentorRepository.save(
            Mentor(
                loginId = "bench-mentor-$suffix",
                password = "x",
                name = "벤치멘토",
                nickname = "bench-mentor-$suffix",
                university = "TestU",
            )
        )
        val mentee = menteeRepository.save(
            Mentee(
                loginId = "bench-mentee-$suffix",
                password = "x",
                name = "벤치멘티",
                nickname = "bench-mentee-$suffix",
                grade = Grade.SECOND,
                school = School.NORMAL,
                hopeMajor = "CS",
            )
        )
        val assignment = assignmentRepository.save(
            Assignment(
                mentor = mentor,
                mentee = mentee,
                templateName = "벤치 템플릿",
                title = "벤치 과제",
                subject = Subject.KOREAN,
                dueDate = LocalDateTime.now().plusDays(1),
                content = "벤치용 과제 내용",
            ).also {
                it.status = AssignmentStatus.SUBMITTED
                it.startTime = LocalDateTime.now().minusHours(2)
                it.endTime = LocalDateTime.now().minusHours(1)
                it.submittedAt = LocalDateTime.now()
            }
        )
        return Triple(mentor.id!!, assignment.id!!, FeedbackCreateReq(content = "벤치 피드백", summary = null))
    }

    private data class BenchResult(
        val successes: Int,
        val alreadyFeedbacked: Int,
        val notSubmitted: Int,
        val dataIntegrityViolation: Int,
        val others: Int,
        val p50Ms: Long,
        val p95Ms: Long,
        val minMs: Long,
        val maxMs: Long,
    )

    private fun printResult(label: String, r: BenchResult) {
        println()
        println("============================================================")
        println("📊 $label (n=$totalRequests)")
        println("============================================================")
        println("DB INSERT 성공                : ${r.successes}")
        println("ASSIGNMENT_ALREADY_FEEDBACKED : ${r.alreadyFeedbacked}")
        println("ASSIGNMENT_NOT_SUBMITTED      : ${r.notSubmitted}")
        println("DataIntegrityViolation 누출   : ${r.dataIntegrityViolation}")
        println("Other exceptions (Deadlock 등): ${r.others}")
        println("응답시간 P50                  : ${r.p50Ms} ms")
        println("응답시간 P95                  : ${r.p95Ms} ms")
        println("응답시간 min / max            : ${r.minMs} / ${r.maxMs} ms")
        println("============================================================")
    }

    @Test
    fun `시나리오 A - 순차 호출 100건 (일반 사용 패턴)`() {
        val (mentorId, assignmentId, req) = freshAssignmentFixture("seq")

        val successes = AtomicInteger()
        val alreadyFeedbacked = AtomicInteger()
        val notSubmitted = AtomicInteger()
        val div = AtomicInteger()
        val others = AtomicInteger()
        val durationsNs = mutableListOf<Long>()

        repeat(totalRequests) {
            val t0 = System.nanoTime()
            try {
                feedbackCreateUseCase.create(mentorId, assignmentId, req)
                successes.incrementAndGet()
            } catch (e: ServiceException) {
                when (e.errorCode) {
                    ErrorCode.ASSIGNMENT_ALREADY_FEEDBACKED -> alreadyFeedbacked.incrementAndGet()
                    ErrorCode.ASSIGNMENT_NOT_SUBMITTED -> notSubmitted.incrementAndGet()
                    else -> others.incrementAndGet()
                }
            } catch (e: DataIntegrityViolationException) {
                div.incrementAndGet()
            } catch (e: Exception) {
                others.incrementAndGet()
            } finally {
                durationsNs.add(System.nanoTime() - t0)
            }
        }

        val sortedMs = durationsNs.sorted().map { it / 1_000_000 }
        val r = BenchResult(
            successes = successes.get(),
            alreadyFeedbacked = alreadyFeedbacked.get(),
            notSubmitted = notSubmitted.get(),
            dataIntegrityViolation = div.get(),
            others = others.get(),
            p50Ms = sortedMs[sortedMs.size / 2],
            p95Ms = sortedMs[(sortedMs.size * 95 / 100).coerceAtMost(sortedMs.size - 1)],
            minMs = sortedMs.first(),
            maxMs = sortedMs.last(),
        )
        printResult("시나리오 A — 순차 호출", r)
        println("DB 내 피드백 총 건수: ${feedbackRepository.count()}")
        println("DB 내 알림 총 건수  : ${notificationRepository.count()}")
    }

    @Test
    fun `시나리오 B - 완전 동시 100건 요청 (극단적 race)`() {
        val (mentorId, assignmentId, req) = freshAssignmentFixture("race")

        val executor = Executors.newFixedThreadPool(totalRequests)
        val startLatch = CountDownLatch(1)
        val doneLatch = CountDownLatch(totalRequests)

        val successes = AtomicInteger()
        val alreadyFeedbacked = AtomicInteger()
        val notSubmitted = AtomicInteger()
        val div = AtomicInteger()
        val others = AtomicInteger()
        val durationsNs = ConcurrentLinkedQueue<Long>()

        repeat(totalRequests) {
            executor.submit {
                runCatching { startLatch.await() }
                val t0 = System.nanoTime()
                try {
                    feedbackCreateUseCase.create(mentorId, assignmentId, req)
                    successes.incrementAndGet()
                } catch (e: ServiceException) {
                    when (e.errorCode) {
                        ErrorCode.ASSIGNMENT_ALREADY_FEEDBACKED -> alreadyFeedbacked.incrementAndGet()
                        ErrorCode.ASSIGNMENT_NOT_SUBMITTED -> notSubmitted.incrementAndGet()
                        else -> others.incrementAndGet()
                    }
                } catch (e: DataIntegrityViolationException) {
                    div.incrementAndGet()
                } catch (e: Exception) {
                    others.incrementAndGet()
                } finally {
                    durationsNs.add(System.nanoTime() - t0)
                    doneLatch.countDown()
                }
            }
        }

        startLatch.countDown()
        check(doneLatch.await(60, TimeUnit.SECONDS)) { "타임아웃" }
        executor.shutdown()

        val sortedMs = durationsNs.toList().sorted().map { it / 1_000_000 }
        val r = BenchResult(
            successes = successes.get(),
            alreadyFeedbacked = alreadyFeedbacked.get(),
            notSubmitted = notSubmitted.get(),
            dataIntegrityViolation = div.get(),
            others = others.get(),
            p50Ms = sortedMs[sortedMs.size / 2],
            p95Ms = sortedMs[(sortedMs.size * 95 / 100).coerceAtMost(sortedMs.size - 1)],
            minMs = sortedMs.first(),
            maxMs = sortedMs.last(),
        )
        printResult("시나리오 B — 완전 동시 호출", r)
        println("DB 내 피드백 총 건수: ${feedbackRepository.count()}")
        println("DB 내 알림 총 건수  : ${notificationRepository.count()}")
    }
}
