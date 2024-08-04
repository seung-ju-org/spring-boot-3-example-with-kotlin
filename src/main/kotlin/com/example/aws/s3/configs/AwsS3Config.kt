package com.example.aws.s3.configs

import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AwsS3Config {
    @Value("\${cloud.aws.s3.region}")
    lateinit var s3Region: String

    @Bean
    fun s3Client() = S3Client {
        region = s3Region
        credentialsProvider = EnvironmentCredentialsProvider()
    }
}
