package com.example.security.configs

import com.example.apps.auth.constants.AuthConstants
import com.example.apps.auth.filters.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
@EnableMethodSecurity(securedEnabled = true)
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val authenticationProvider: AuthenticationProvider
) {
    @Bean
    fun filterChain(http: HttpSecurity) = http
        .csrf { it.disable() }
        .cors {}
        .authorizeHttpRequests {
            it
                .requestMatchers(
                    "/api/**",
                    "/error",
                    "/api-docs",
                    "/api-docs/swagger-config",
                    "/swagger-ui/**",
                ).permitAll()
                .anyRequest().authenticated()
        }
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        .formLogin { it.disable() }
        .headers { it.frameOptions { frameOptions -> frameOptions.sameOrigin() } }
        .httpBasic(Customizer.withDefaults())
        .build()!!

    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.addAllowedOriginPattern("*")
        config.addAllowedMethod("*")
        config.addAllowedHeader(AuthConstants.AUTH_HEADER)
        config.addAllowedHeader("*")
        config.allowCredentials = true
        config.maxAge = 3600L
        config.addExposedHeader(AuthConstants.AUTH_HEADER)
        source.registerCorsConfiguration("/**", config)

        return CorsFilter(source)
    }
}
