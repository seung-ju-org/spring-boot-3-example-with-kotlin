package com.example.apps.auth.repositories

import com.example.apps.auth.domains.RefreshToken
import com.example.apps.auth.serializers.RedisRefreshTokenSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class RefreshTokenRepository(
    private val redisTemplate: RedisTemplate<String, RefreshToken>
) {
    @Value("\${jwt.refresh-token-expires}")
    var refreshTokenExpires: Int = 0

    init {
        val jsonSerializer = RedisRefreshTokenSerializer()

        redisTemplate.setDefaultSerializer(jsonSerializer)
        redisTemplate.valueSerializer = jsonSerializer
        redisTemplate.hashValueSerializer = jsonSerializer
    }

    fun getValueKey(refreshToken: RefreshToken) = "user:${refreshToken.userId}:token:${refreshToken.jti}"

    fun getSetKey(userId: Long) = "user:$userId:tokens"

    fun getSetKey(refreshToken: RefreshToken) = getSetKey(refreshToken.userId!!)

    fun save(refreshToken: RefreshToken): RefreshToken {
        val key = getValueKey(refreshToken)
        val valueOperations = redisTemplate.opsForValue()
        valueOperations[key] = refreshToken
        redisTemplate.expire(key, refreshTokenExpires.toLong(), TimeUnit.MILLISECONDS)
        redisTemplate.opsForSet().add(getSetKey(refreshToken), refreshToken)
        return refreshToken
    }

    fun find(refreshToken: RefreshToken) = redisTemplate.opsForValue()[getValueKey(refreshToken)]

    fun findAllByUserId(userId: Long): MutableSet<RefreshToken>? = redisTemplate.opsForSet().members(getSetKey(userId))

    fun getExpiresIn(refreshToken: RefreshToken) = redisTemplate.getExpire(getValueKey(refreshToken), TimeUnit.MILLISECONDS)

    fun delete(refreshToken: RefreshToken) {
        val valueKey = getValueKey(refreshToken)
        redisTemplate.delete(valueKey)
        val setKey = getSetKey(refreshToken)
        redisTemplate.opsForSet().remove(setKey, refreshToken)
    }

    fun deleteAllByUserId(userId: Long) {
        val refreshTokens = findAllByUserId(userId)
        refreshTokens?.forEach { refreshToken ->
            delete(refreshToken)
        }
    }
}
