package com.example.apps.auth.services

import com.example.apps.auth.constants.AuthConstants
import com.example.apps.auth.domains.RefreshToken
import com.example.apps.auth.dtos.JwtDto
import com.example.apps.auth.exceptions.InvalidTokenException
import com.example.apps.auth.exceptions.NotFoundRefreshTokenException
import com.example.apps.auth.repositories.RefreshTokenRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

@Service
class JwtService(
    private val secretKey: SecretKey,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    @Value("\${jwt.access-token-expires}")
    var accessTokenExpires: Int = 0

    @Value("\${jwt.refresh-token-expires}")
    var refreshTokenExpires: Int = 0

    fun findAllByUserId(userId: Long) = refreshTokenRepository.findAllByUserId(userId)

    fun createRefreshToken(userId: Long): RefreshToken {
        val now = Date()
        val expiration = Date(now.time + refreshTokenExpires)

        return refreshTokenRepository.save(
            RefreshToken(
                jti = UUID.randomUUID().toString(),
                refreshToken = Jwts.builder().signWith(secretKey).setIssuedAt(now)
                    .setExpiration(expiration).setSubject(userId.toString()).compact(),
                userId,
                createdAt = LocalDateTime.now(),
                expiresAt = LocalDateTime.ofInstant(expiration.toInstant(), ZoneId.systemDefault())
            )
        )
    }

    fun createAccessToken(refreshToken: RefreshToken): String {
        val refreshTokenEntity: RefreshToken = refreshTokenRepository.find(refreshToken) ?: throw NotFoundRefreshTokenException()

        val now = Date()
        val expiration = Date(now.time + accessTokenExpires)

        return Jwts.builder().signWith(secretKey).setIssuedAt(now)
            .setExpiration(expiration).setSubject(refreshTokenEntity.userId.toString()).compact()
    }

    fun createJwt(userId: Long): JwtDto.Response {
        val refreshToken: RefreshToken = createRefreshToken(userId)
        val accessToken = createAccessToken(refreshToken)

        val now = Date()

        val expiration: Date = extract(accessToken).expiration
        val refreshTokenExpiresIn: Long = refreshTokenRepository.getExpiresIn(refreshToken)

        return JwtDto.Response(
            tokenType = AuthConstants.JWT_TOKEN_TYPE,
            jti = refreshToken.jti,
            accessToken = accessToken,
            accessTokenExpiresIn = expiration.time - now.time,
            accessTokenExpiresAt = Instant.ofEpochMilli(expiration.time)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime(),
            refreshToken = refreshToken.refreshToken,
            refreshTokenExpiresIn = refreshTokenExpiresIn,
            refreshTokenExpiresAt = Instant.ofEpochMilli(now.time + refreshTokenExpiresIn)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime(),
            createdAt = LocalDateTime.ofInstant(now.toInstant(), ZoneId.systemDefault())
        )
    }

    fun renewalToken(jti: String, refreshToken: String): JwtDto.Response {
        val userId = extractId(refreshToken)
        val refreshTokenEntity = refreshTokenRepository.find(
            RefreshToken(
                jti,
                refreshToken,
                userId
            )
        ) ?: throw NotFoundRefreshTokenException()
        if (refreshTokenEntity.refreshToken != refreshToken) throw InvalidTokenException()
        return createJwt(refreshTokenEntity.userId!!)
    }

    fun expireToken(jti: String, refreshToken: String, userId: Long) {
        val refreshTokenEntity = refreshTokenRepository.find(
            RefreshToken(
                jti,
                refreshToken,
                userId
            )
        ) ?: throw NotFoundRefreshTokenException()

        if (
            refreshTokenEntity.refreshToken != refreshToken ||
            refreshTokenEntity.userId != userId
        ) throw InvalidTokenException()

        refreshTokenRepository.delete(refreshTokenEntity)
    }

    fun expireTokensByUserId(userId: Long) = refreshTokenRepository.deleteAllByUserId(userId)

    fun extract(token: String): Claims {
        try {
            return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).body
        } catch (e: JwtException) {
            throw InvalidTokenException()
        }
    }

    fun extractId(token: String) = extract(token).subject.toLong()
}
