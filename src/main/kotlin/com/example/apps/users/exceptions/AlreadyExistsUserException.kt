package com.example.apps.users.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.CONFLICT)
class AlreadyExistsUserException : RuntimeException() {
    override val message: String
        get() = "Already exists user"
}
