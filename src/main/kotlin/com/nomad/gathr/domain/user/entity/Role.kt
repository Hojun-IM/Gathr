package com.nomad.gathr.domain.user.entity

enum class Role(val key: String, val title: String) {
    UNKNOWN("ROLE_UNKNOWN", "비회원 사용자"),
    USER("ROLE_USER", "일반 사용자"),
    ADMIN("ROLE_ADMIN", "관리자");
}