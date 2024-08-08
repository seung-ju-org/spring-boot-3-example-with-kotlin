package com.example.apps.boards.repositories

import com.example.apps.boards.domains.BoardCategory
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository

interface BoardCategoryRepository : JpaRepository<BoardCategory, Long>, KotlinJdslJpqlExecutor
