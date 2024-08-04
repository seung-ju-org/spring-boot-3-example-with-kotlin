package com.example.apps.files.repositories

import com.example.apps.files.domains.File
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository

interface FileRepository : JpaRepository<File, Long>, KotlinJdslJpqlExecutor
