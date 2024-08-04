package com.example.apps.users.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class IncorrectPasswordException : RuntimeException() {
    override val message: String
        get() = "Incorrect password"
}
