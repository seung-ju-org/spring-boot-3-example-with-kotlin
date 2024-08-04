package com.example.apps.auth.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundRefreshTokenException : RuntimeException() {
    override val message: String
        get() = "Not found refresh token"
}
