package com.example.apps.boards.controllers

import com.example.apps.boards.dtos.BoardDto
import com.example.apps.boards.services.BoardService
import com.example.security.enums.Authority
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/boards")
@Tag(name = "Board", description = "Board API")
class BoardController(
    private val boardService: BoardService
) {
    @PostMapping
    @Operation(summary = "Create board", description = "Create board")
    @Secured(Authority.ROLES.USER)
    fun create(
        @RequestBody create: BoardDto.Create
    ) = boardService.create(create)

    @GetMapping
    @Operation(summary = "Get boards", description = "Get boards")
    fun findAll(
        @ParameterObject pageable: Pageable,
        @ParameterObject request: BoardDto.Request
    ) = boardService.findAll(pageable, request)

    @GetMapping("/{id}")
    @Operation(summary = "Get board", description = "Get board")
    fun findById(
        @PathVariable("id") id: Long
    ) = boardService.findById(id)

    @PatchMapping("/{id}")
    @Operation(summary = "Update board", description = "Update board")
    @Secured(Authority.ROLES.USER)
    fun update(
        @PathVariable("id") id: Long,
        @RequestBody update: BoardDto.Update
    ) = boardService.update(id, update)

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete board", description = "Delete board")
    @Secured(Authority.ROLES.USER)
    fun deleteById(
        @PathVariable("id") id: Long,
    ) = boardService.deleteById(id)
}
