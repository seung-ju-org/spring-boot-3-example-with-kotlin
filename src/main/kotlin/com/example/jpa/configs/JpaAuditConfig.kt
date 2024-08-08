package com.example.jpa.configs

import com.example.apps.auth.domains.CustomUserDetails
import com.example.apps.users.domains.User
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder
import java.util.Optional

@Configuration
@EnableJpaAuditing
class JpaAuditConfig {
    @Bean
    fun auditorProvider(): AuditorAware<User> {
        return AuditorAware<User> {
            Optional.ofNullable(SecurityContextHolder.getContext())
                .map { (it.authentication.principal as CustomUserDetails).user }
        }
    }
}
