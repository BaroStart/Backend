package com.barostartbe.domain.mentor.entity


import com.barostartbe.domain.auth.dto.SignupRequestDto
import com.barostartbe.domain.user.entity.Role
import com.barostartbe.domain.user.entity.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "mentors")
class Mentor(
    loginId: String,
    password: String,
    name: String,
    nickname: String,

    @Column(nullable = false) var university: String? = null
) : User(
    loginId = loginId,
    password = password,
    name = name,
    nickname = nickname,
    role = Role.MENTOR
){
    companion object {
        fun from(request: SignupRequestDto): User {
            return Mentor(
                loginId = request.loginId,
                password = request.password,
                name = request.name,
                nickname = request.nickname,
                university = request.university
            )
        }
    }
}