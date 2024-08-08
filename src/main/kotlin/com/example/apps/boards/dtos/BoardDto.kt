package com.example.apps.boards.dtos

import com.example.apps.users.dtos.UserDto
import java.time.LocalDateTime

class BoardDto {
    data class Request(
        var boardCategoryId: Long? = null,
        var title: String? = null
    )

    data class Create(
        var boardCategoryId: Long? = null,
        var title: String? = null,
        var contents: String? = null,
    )

    data class Response(
        var id: Long? = null,
        var boardCategory: BoardCategoryDto.SimpleResponse? = null,
        var title: String? = null,
        var createdByUser: UserDto.SimpleResponse? = null,
        var updatedByUser: UserDto.SimpleResponse? = null,
        var createdAt: LocalDateTime? = null,
        var updatedAt: LocalDateTime? = null
    )

    data class DetailResponse(
        var id: Long? = null,
        var boardCategory: BoardCategoryDto.SimpleResponse? = null,
        var title: String? = null,
        var contents: String? = null,
        var createdByUser: UserDto.SimpleResponse? = null,
        var updatedByUser: UserDto.SimpleResponse? = null,
        var createdAt: LocalDateTime? = null,
        var updatedAt: LocalDateTime? = null
    )

    data class Update(
        var boardCategoryId: Long? = null,
        var title: String? = null,
        var contents: String? = null,
    )
}
