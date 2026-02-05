package com.barostartbe.domain.mentee.entity

import com.barostartbe.domain.auth.dto.SignupRequestDto
import com.barostartbe.domain.user.entity.Role
import com.barostartbe.domain.user.entity.User
import com.barostartbe.global.error.exception.ServiceException
import com.barostartbe.global.response.type.ErrorCode
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "mentees")
class Mentee(
    loginId: String,
    password: String,
    name: String,
    nickname: String,

    @Column(nullable = false) var grade: Grade? = null,
    @Column(nullable = false) var school: School? = null,
    @Column(nullable = false) var hopeMajor: String? = null,
) : User(
    loginId = loginId,
    password = password,
    name = name,
    nickname = nickname,
    role = Role.MENTEE
){
    companion object {
        fun from(request: SignupRequestDto): Mentee {
            val grade = when(request.grade){
                "FIRST" -> Grade.FIRST
                "SECOND" -> Grade.SECOND
                "THIRD" -> Grade.THIRD
                "ETC" -> Grade.ETC
                else -> throw ServiceException(ErrorCode.BAD_PARAMETER)
            }
            val school = when(request.school){
                "NORMAL" -> School.NORMAL
                "SPECIAL" -> School.SPECIAL
                else -> throw ServiceException(ErrorCode.BAD_PARAMETER)
            }
            return Mentee(
                loginId = request.loginId,
                password = request.password,
                name = request.name,
                nickname = request.nickname,
                grade = grade,
                school = school,
                hopeMajor = request.hopeMajor
            )
        }
    }
}