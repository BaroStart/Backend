package com.barostartbe.domain.user.entity

import com.barostartbe.domain.auth.dto.SignupRequestDto
import com.barostartbe.global.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.Entity
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type")
@Table(name = "users")
open class User(
    @Column(nullable = false, length = 20, unique = true) val loginId: String? = null,
    @Column(nullable = false) private var password: String? = null,
    @Column(nullable = false, length = 10) val name: String? = null,
    @Column(nullable = false) val role : Role? = null,
    @Column(nullable = false, length = 30, unique = true) var nickname: String? = null
) : BaseEntity(), UserDetails{
    override fun getUsername(): String = loginId!!
    override fun getPassword(): String = password!!
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_" + role!!.name))
    }
    fun setPassword(password: String) {
        this.password = password
    }
}