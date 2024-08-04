package com.example.swagger.configs

import com.example.apps.auth.constants.AuthConstants
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Value("\${springdoc.swagger-ui.info.title}")
    lateinit var title: String

    @Value("\${springdoc.swagger-ui.info.description}")
    lateinit var description: String

    @Value("\${springdoc.swagger-ui.info.version}")
    lateinit var version: String

    @Bean
    fun openApi(): OpenAPI = OpenAPI()
        .components(
            Components()
                .addSecuritySchemes(
                    "Basic Authentication",
                    SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme(AuthConstants.BASIC_TOKEN_TYPE)
                )
                .addSecuritySchemes(
                    "Bearer Authentication",
                    SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .bearerFormat("JWT")
                        .scheme(AuthConstants.JWT_TOKEN_TYPE)
                )
        )
        .addSecurityItem(
            SecurityRequirement()
                .addList("Bearer Authentication")
        )
        .addSecurityItem(
            SecurityRequirement()
                .addList("Basic Authentication")
        )
        .info(info())

    fun info(): Info = Info()
        .title(title)
        .description(description)
        .version(version)
}
