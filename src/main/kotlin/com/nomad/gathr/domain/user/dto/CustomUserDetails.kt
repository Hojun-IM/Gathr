package com.nomad.gathr.domain.user.dto

import com.nomad.gathr.domain.user.entity.AccountStatus
import com.nomad.gathr.domain.user.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    private val user: User
) : UserDetails {
    val id: Long = user.id!!

    override fun getUsername(): String = user.username
    override fun getPassword(): String = user.password
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = user.accountStatus == AccountStatus.ACTIVE
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority(user.role.key))
    }

    fun getUser(): User = user
}