package com.barostartbe.domain.notification.usecase

import com.barostartbe.domain.notification.dto.request.NoticeRequest
import com.barostartbe.domain.notification.entity.Notification
import com.barostartbe.domain.notification.entity.enums.Type
import com.barostartbe.domain.notification.repository.NotificationRepository
import com.barostartbe.domain.user.repository.UserRepository
import com.barostartbe.global.annotation.CommandUseCase
import com.barostartbe.global.sse.dto.base.SseEvent
import com.barostartbe.global.sse.usecase.SsePublishUseCase
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.PageRequest

private val log = KotlinLogging.logger {}

@CommandUseCase
class SendNoticeUseCase(
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository,
    private val ssePublishUseCase: SsePublishUseCase
) {
    fun execute(noticeRequest: NoticeRequest) {
        log.info { "공지사항 발송 시작 - 제목: ${noticeRequest.title}" }

        val sseEvent = SseEvent(
            topic = null,
            event = "notice",
            data = noticeRequest
        )
        ssePublishUseCase.broadcast(sseEvent)

        var page = 0
        val batchSize = 1000
        var totalSent = 0

        while (true) {
            val pageable = PageRequest.of(page, batchSize)
            val users = userRepository.findAll(pageable)

            if (users.isEmpty) break

            val notifications = users.content.map { user ->
                Notification.of(
                    title = noticeRequest.title,
                    message = noticeRequest.content,
                    type = Type.NOTICE,
                    receiver = user
                )
            }
            notificationRepository.saveAll(notifications)
            totalSent += notifications.size

            if (!users.hasNext()) break
            page++
        }

        log.info { "공지사항 발송 완료 - 총 ${totalSent}명에게 저장됨" }
    }
}
