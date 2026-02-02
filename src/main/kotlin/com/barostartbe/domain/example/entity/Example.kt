package com.barostartbe.domain.example.entity

import com.barostartbe.domain.example.dto.request.ExampleReq
import com.barostartbe.global.common.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "example")
class Example(

    // 이름
    @Column(nullable = false, length = 100)
    val name: String
) : BaseEntity() {
    companion object {
        fun from(exampleReq: ExampleReq): Example {
            return Example(
                name = exampleReq.name
            )
        }
    }
}
