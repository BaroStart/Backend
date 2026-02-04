package com.barostartbe.domain.user.entity

import com.barostartbe.domain.auth.dto.SignupRequestDto
import com.barostartbe.global.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false, length = 20, unique = true) val loginId: String,
    @Column(nullable = false) private var password: String,
    @Column(nullable = false, length = 10) val name: String,
    @Column(nullable = false) val role : Role,
    @Column(nullable = false, length = 30, unique = true) var nickname: String
) : BaseEntity(), UserDetails{
    override fun getUsername(): String = loginId
    override fun getPassword(): String = password
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_" + role.name))
    }
    fun setPassword(password: String) {
        this.password = password
    }

    companion object {
        fun from(signupRequestDto: SignupRequestDto): User {
            return User(
                loginId = signupRequestDto.loginId,
                password = signupRequestDto.password,
                name = signupRequestDto.name,
                nickname = signupRequestDto.nickname,
                role = Role.MENTEE
            )
        }
    }
}