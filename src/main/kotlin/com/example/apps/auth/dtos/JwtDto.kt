package com.example.apps.auth.dtos

import java.time.LocalDateTime

class JwtDto {
    data class Response(
        var jti: String? = null,
        var tokenType: String? = null,
        var accessToken: String? = null,
        var accessTokenExpiresAt: LocalDateTime? = null,
        var accessTokenExpiresIn: Long? = null,
        var refreshToken: String? = null,
        var refreshTokenExpiresAt: LocalDateTime? = null,
        var refreshTokenExpiresIn: Long? = null,
        var createdAt: LocalDateTime? = null
    )
}
