package com.example.apps.boards.repositories

import com.example.apps.boards.domains.Board
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository

interface BoardRepository : JpaRepository<Board, Long>, KotlinJdslJpqlExecutor
