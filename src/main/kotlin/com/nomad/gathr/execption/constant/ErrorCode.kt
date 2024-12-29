package com.nomad.gathr.execption.constant

enum class ErrorCode(
    val status: String,
    val code: Int,
    val message: String
) {
    // Common
    INVALID_INPUT_VALUE("COMMON", 400, "유효하지 않은 입력 값입니다."),
    METHOD_NOT_ALLOWED("COMMON", 405, "지원하지 않는 메소드입니다."),
    ENTITY_NOT_FOUND("COMMON", 400, "해당 엔티티를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR("COMMON", 500, "서버에 오류가 발생했습니다."),
    INVALID_TYPE_VALUE("COMMON", 400, "타입이 일치하지 않습니다."),
    UNAUTHORIZED("COMMON", 403, "권한이 없습니다."),

    // User
    USERNAME_DUPLICATION("USER", 400, "이미 사용중인 아이디입니다."),
    EMAIL_DUPLICATION("USER", 400, "이미 사용중인 이메일입니다."),
    SIGNIN_INPUT_INVALID("USER", 400, "아이디 또는 비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND("USER", 400, "사용자를 찾을 수 없습니다."),
    REFRESH_TOKEN_NOT_FOUND("USER", 400, "재발급 토큰이 존재하지 않습니다."),
    REFRESH_TOKEN_NOT_MATCHED("USER", 400, "재발급 토큰이 일치하지 않습니다."),
    REFRESH_TOKEN_EXPIRED("USER", 400, "재발급 토큰이 만료되었습니다."),
    REFRESH_TOKEN_INVALID("USER", 400, "재발급 토큰이 유효하지 않습니다."),
    ACCESS_TOKEN_EXPIRED("USER", 400, "액세스 토큰이 만료되었습니다."),
    ACCESS_TOKEN_INVALID("USER", 400, "액세스 토큰이 유효하지 않습니다."),
    ACCESS_TOKEN_NOT_FOUND("USER", 400, "액세스 토큰이 존재하지 않습니다."),
    SIGNIN_REQUIRED("USER", 400, "로그인이 필요합니다."),
    SIGNOUT_REQUIRED("USER", 400, "로그아웃이 필요합니다."),
    SIGNIN_FAILED("USER", 400, "로그인에 실패했습니다."),
    SIGNOUT_FAILED("USER", 400, "로그아웃에 실패했습니다."),
    PASSWORD_CHANGE_REQUIRED("USER", 400, "비밀번호 변경이 필요합니다."),
    PASSWORD_CHANGE_FAILED("USER", 400, "비밀번호 변경에 실패했습니다."),

    // Recruitment
    RECRUITMENT_NOT_FOUND("RECRUITMENT", 400, "모집을 찾을 수 없습니다."),
    RECRUITMENT_CLOSED("RECRUITMENT", 400, "모집이 종료되었습니다."),
    RECRUITMENT_OPENED("RECRUITMENT", 400, "모집이 열려있습니다."),
    RECRUITMENT_DATE_INVALID("RECRUITMENT", 400, "모집 날짜가 유효하지 않습니다."),
    RECRUITMENT_PARTICIPANT_MAX("RECRUITMENT", 400, "모집 인원이 초과되었습니다."),

    // Application
    APPLICATION_NOT_FOUND("APPLICATION", 400, "지원서를 찾을 수 없습니다."),
    INVALID_STATUS_CHANGE("APPLICATION", 400, "이미 처리된 지원서는 상태를 변경할 수 없습니다."),
}