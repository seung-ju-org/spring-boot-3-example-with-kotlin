package com.example.apps.auth.domains

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.time.LocalDateTime

@RedisHash("refresh-token")
class RefreshToken(
    @Id
    var jti: String,
    val refreshToken: String,
    val userId: Long?,
    val createdAt: LocalDateTime? = null,
    val expiresAt: LocalDateTime? = null
)
