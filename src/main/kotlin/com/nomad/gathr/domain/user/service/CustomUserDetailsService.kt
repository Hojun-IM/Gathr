package com.nomad.gathr.domain.user.service

import com.nomad.gathr.domain.user.dto.CustomUserDetails
import com.nomad.gathr.domain.user.entity.User
import com.nomad.gathr.domain.user.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user: User = userRepository.findByUsername(username)
            .orElseThrow { UsernameNotFoundException("유효하지 않는 아이디: $username") }
        return CustomUserDetails(user)
    }
}