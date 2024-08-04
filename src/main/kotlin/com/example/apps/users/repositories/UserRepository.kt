package com.example.apps.users.repositories

import com.example.apps.users.domains.User
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>, KotlinJdslJpqlExecutor
