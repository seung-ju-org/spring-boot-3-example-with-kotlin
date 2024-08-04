package com.example.apps.users.repositories

import com.example.apps.users.domains.Account
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository

interface AccountRepository : JpaRepository<Account, Long>, KotlinJdslJpqlExecutor
