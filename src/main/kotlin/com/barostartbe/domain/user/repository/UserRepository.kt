package com.barostartbe.domain.user.repository

import com.barostartbe.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>{
    fun findUserByLoginId(loginId: String): User?
    fun existsUserByLoginId(loginId: String): Boolean
}