package com.barostartbe.domain.example.domain

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
)
