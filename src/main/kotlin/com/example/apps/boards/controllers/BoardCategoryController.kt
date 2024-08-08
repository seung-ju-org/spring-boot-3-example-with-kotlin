package com.example.apps.boards.controllers

import com.example.apps.boards.dtos.BoardCategoryDto
import com.example.apps.boards.services.BoardCategoryService
import com.example.security.enums.Authority
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
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
@RequestMapping("/api/board-categories")
@Tag(name = "Board Category", description = "Board Category API")
class BoardCategoryController(
    private val boardCategoryService: BoardCategoryService
) {
    @PostMapping
    @Operation(summary = "Create board category", description = "Create board category")
    @Secured(Authority.ROLES.USER)
    fun create(
        @RequestBody create: BoardCategoryDto.Create
    ) = boardCategoryService.create(create)

    @GetMapping
    @Operation(summary = "Get board categories", description = "Get board categories")
    fun findAll() = boardCategoryService.findAll()

    @GetMapping("/{id}")
    @Operation(summary = "Get board category", description = "Get board category")
    fun findById(
        @PathVariable("id") id: Long
    ) = boardCategoryService.findById(id)

    @PatchMapping("/{id}")
    @Operation(summary = "Update board category", description = "Update board category")
    @Secured(Authority.ROLES.USER)
    fun update(
        @PathVariable("id") id: Long,
        @RequestBody update: BoardCategoryDto.Update
    ) = boardCategoryService.update(id, update)

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete board category", description = "Delete board category")
    @Secured(Authority.ROLES.USER)
    fun deleteById(
        @PathVariable("id") id: Long,
    ) = boardCategoryService.deleteById(id)
}
