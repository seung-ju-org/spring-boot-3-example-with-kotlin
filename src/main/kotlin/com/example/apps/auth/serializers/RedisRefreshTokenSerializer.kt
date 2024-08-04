package com.example.apps.auth.serializers

import com.example.apps.auth.domains.RefreshToken
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.jsonwebtoken.io.DeserializationException
import io.jsonwebtoken.io.SerializationException
import org.springframework.data.redis.serializer.RedisSerializer

class RedisRefreshTokenSerializer : RedisSerializer<RefreshToken> {
    private val objectMapper = jacksonObjectMapper()

    init {
        objectMapper.registerModules(JavaTimeModule())
    }

    override fun serialize(refreshToken: RefreshToken?): ByteArray {
        try {
            return objectMapper.writeValueAsBytes(refreshToken)
        } catch (e: Exception) {
            throw SerializationException("Error serializing RefreshToken to JSON.", e)
        }
    }

    override fun deserialize(bytes: ByteArray?): RefreshToken? {
        if (bytes == null || bytes.isEmpty()) {
            return null
        }

        try {
            return objectMapper.readValue(bytes, RefreshToken::class.java)
        } catch (e: Exception) {
            throw DeserializationException("Error deserializing RefreshToken from JSON.", e)
        }
    }
}
