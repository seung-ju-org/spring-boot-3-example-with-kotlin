package com.example.apps.auth.configs

import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.nio.charset.StandardCharsets
import javax.crypto.SecretKey

@Configuration
class JwtConfig {
    @Value("\${jwt.secret-key}")
    lateinit var secretKey: String

    @Bean
    fun secretKey(): SecretKey {
        return Keys.hmacShaKeyFor(secretKey.toByteArray(StandardCharsets.UTF_8))
    }
}
