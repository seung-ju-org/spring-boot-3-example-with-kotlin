package com.example.apps.auth.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.FORBIDDEN)
class InvalidTokenException : RuntimeException() {
    override val message: String
        get() = "Invalid token"
}
