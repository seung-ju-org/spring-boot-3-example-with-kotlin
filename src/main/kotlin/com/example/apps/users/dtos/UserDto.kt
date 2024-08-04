package com.example.apps.users.dtos

import com.example.apps.files.dtos.FileDto
import java.time.LocalDateTime

class UserDto {
    data class Request(
        var email: String? = null,
        var username: String? = null,
        var firstName: String? = null,
        var lastName: String? = null,
        var nickname: String? = null,
        var phone: String? = null,
    )

    data class Create(
        var email: String? = null,
        var username: String,
        var firstName: String? = null,
        var lastName: String? = null,
        var password: String? = null,
        var nickname: String? = null,
        var profileFileId: Long? = null,
        var phone: String? = null,
    )

    data class Response(
        var id: Long? = null,
        var email: String? = null,
        var username: String,
        var firstName: String? = null,
        var lastName: String? = null,
        var nickname: String? = null,
        var profileFile: FileDto.Response? = null,
        var phone: String? = null,
        var createdAt: LocalDateTime? = null,
        var updatedAt: LocalDateTime? = null,
    )

    data class DetailResponse(
        var id: Long? = null,
        var email: String? = null,
        var username: String? = null,
        var firstName: String? = null,
        var lastName: String? = null,
        var nickname: String? = null,
        var profileFile: FileDto.Response? = null,
        var phone: String? = null,
        var createdAt: LocalDateTime? = null,
        var updatedAt: LocalDateTime? = null,
    )

    data class Update(
        var firstName: String? = null,
        var lastName: String? = null,
        var password: String? = null,
        var nickname: String? = null,
        var profileFileId: Long? = null,
        var phone: String? = null,
    )
}
