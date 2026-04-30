package com.barostartbe.bench

import com.oracle.bmc.objectstorage.ObjectStorage
import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

// bench 프로필에서 OciConfig 가 비활성화되므로, ObjectStorage Bean 을 mock 으로 대체
@TestConfiguration
class BenchTestConfig {

    @Bean
    fun objectStorage(): ObjectStorage = mockk(relaxed = true)
}
