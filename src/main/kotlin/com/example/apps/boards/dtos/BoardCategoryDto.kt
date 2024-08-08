package com.example.apps.boards.dtos

import com.example.apps.users.dtos.UserDto
import java.time.LocalDateTime

class BoardCategoryDto {
    data class Create(
        var name: String? = null
    )

    data class Response(
        var id: Long? = null,
        var name: String? = null,
        var createdByUser: UserDto.SimpleResponse? = null,
        var updatedByUser: UserDto.SimpleResponse? = null,
        var createdAt: LocalDateTime = LocalDateTime.now(),
        var updatedAt: LocalDateTime = LocalDateTime.now()
    )

    data class DetailResponse(
        var id: Long? = null,
        var name: String? = null,
        var createdByUser: UserDto.SimpleResponse? = null,
        var updatedByUser: UserDto.SimpleResponse? = null,
        var createdAt: LocalDateTime = LocalDateTime.now(),
        var updatedAt: LocalDateTime = LocalDateTime.now()
    )

    data class SimpleResponse(
        var id: Long? = null,
        var name: String? = null
    )

    data class Update(
        var name: String? = null
    )
}
