package com.example.apps.users.controllers

import com.example.apps.users.dtos.UserDto
import com.example.apps.users.services.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User API")
class UserController(
    private val userService: UserService
) {
    @GetMapping
    @Operation(summary = "Get users", description = "Get users")
    fun findAll(
        @ParameterObject pageable: Pageable,
        @ParameterObject request: UserDto.Request
    ) = userService.findAll(pageable, request)

    @GetMapping("{id}")
    @Operation(summary = "Get user", description = "Get user")
    fun findById(@PathVariable("id") id: Long) = userService.findById(id)
}
