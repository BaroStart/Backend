package com.barostartbe.domain.example.entity

import com.barostartbe.domain.example.dto.request.ExampleReq
import jakarta.persistence.*

@Entity
@Table(name = "example")
class Example(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    // 이름
    @Column(nullable = false, length = 100)
    val name: String
) {
    companion object {
        fun from(exampleReq: ExampleReq): Example {
            return Example(
                name = exampleReq.name
            )
        }
    }
}
