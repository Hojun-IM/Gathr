package com.nomad.gathr.config

import com.nomad.gathr.domain.authentication.service.AuthService
import com.nomad.gathr.domain.user.service.CustomUserDetailsService
import com.nomad.gathr.security.filter.JwtAuthenticationFilter
import com.nomad.gathr.security.handler.JwtAccessDeniedHandler
import com.nomad.gathr.security.service.JwtService
import com.nomad.gathr.security.util.JwtUtil
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customUserDetailsService: CustomUserDetailsService,
    private val jwtService: JwtService,
    private val jwtUtil: JwtUtil,
    private val accessDeniedHandler: JwtAccessDeniedHandler

) {

    companion object {
        val  PERMITTED_URLS = arrayOf(
            "/api/v1/auth/**",
            "/api/login"
        )

        val PERMITTED_ROLES = arrayOf(
            "USER",
            "ADMIN"
        )
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it
                    .requestMatchers(*PERMITTED_URLS).permitAll()  // permitAll() 메서드를 사용하여 허용할 URL을 지정
                    .anyRequest().hasAnyRole(*PERMITTED_ROLES)     // hasAnyRole() 메서드를 사용하여 허용할 권한을 지정
            }
            .exceptionHandling {
                it.accessDeniedHandler(accessDeniedHandler)
            }
            .addFilterBefore(
                JwtAuthenticationFilter(customUserDetailsService, jwtService, jwtUtil),
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }
}