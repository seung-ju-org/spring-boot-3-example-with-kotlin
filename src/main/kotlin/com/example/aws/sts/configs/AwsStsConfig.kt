package com.example.aws.sts.configs

import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.sts.StsClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AwsStsConfig {
    @Bean
    fun stsClient() = StsClient {
        credentialsProvider = EnvironmentCredentialsProvider()
    }
}
