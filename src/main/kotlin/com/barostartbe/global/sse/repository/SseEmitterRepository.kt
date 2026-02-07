package com.barostartbe.global.sse.repository

import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

@Component
class SseEmitterRepository {

    private val topicEmitters = ConcurrentHashMap<String, MutableSet<SseEmitter>>()
    private val sessionTopics = ConcurrentHashMap<String, MutableSet<String>>()
    private val sessionEmitters = ConcurrentHashMap<String, SseEmitter>()
    private val emitterToSession = ConcurrentHashMap<SseEmitter, String>()

    fun subscribe(sessionId: String, topics: Set<String>, emitter: SseEmitter): SseEmitter {
        val oldEmitter = sessionEmitters[sessionId]
        if (oldEmitter != null && oldEmitter != emitter) {
            sessionTopics[sessionId]?.let { oldTopics ->
                removeEmitterFromTopics(oldEmitter, oldTopics)
            }
            emitterToSession.remove(oldEmitter)
        }

        emitter.onCompletion { unsubscribeByEmitter(emitter) }
        emitter.onTimeout { unsubscribeByEmitter(emitter) }
        emitter.onError { _ ->
            unsubscribeByEmitter(emitter)
        }

        sessionTopics[sessionId] = CopyOnWriteArraySet(topics)
        sessionEmitters[sessionId] = emitter
        emitterToSession[emitter] = sessionId

        for (topic in topics) {
            topicEmitters.computeIfAbsent(topic) { CopyOnWriteArraySet() }.add(emitter)
        }

        return emitter
    }

    fun unsubscribe(sessionId: String) {
        sessionEmitters[sessionId]?.let { emitter ->
            unsubscribeByEmitter(emitter)
        }
    }

    private fun unsubscribeByEmitter(emitter: SseEmitter) {
        val sessionId = emitterToSession.remove(emitter) ?: return

        val removed = sessionEmitters.remove(sessionId, emitter)
        if (!removed) {
            return
        }

        val topics = sessionTopics.remove(sessionId)
        if (topics != null) {
            removeEmitterFromTopics(emitter, topics)
        }
    }

    private fun removeEmitterFromTopics(emitter: SseEmitter, topics: Set<String>?) {
        if (topics == null) return
        for (topic in topics) {
            val emitters = topicEmitters[topic]
            if (emitters != null) {
                emitters.remove(emitter)
                if (emitters.isEmpty()) {
                    topicEmitters.remove(topic)
                }
            }
        }
    }

    fun removeEmitter(emitter: SseEmitter) {
        if (emitterToSession.containsKey(emitter)) {
            unsubscribeByEmitter(emitter)
        } else {
            for (emitters in topicEmitters.values) {
                emitters.remove(emitter)
            }
        }
    }

    fun getTopicEmitters(topic: String): Set<SseEmitter> {
        return topicEmitters[topic] ?: emptySet()
    }

    fun getAllEmitters(): Set<SseEmitter> {
        return CopyOnWriteArraySet(sessionEmitters.values)
    }

    fun getConnectedSessionCount(): Int = sessionEmitters.size

    fun getTopicCount(): Int = topicEmitters.size

    fun clearAll() {
        topicEmitters.clear()
        sessionTopics.clear()
        sessionEmitters.clear()
        emitterToSession.clear()
    }
}