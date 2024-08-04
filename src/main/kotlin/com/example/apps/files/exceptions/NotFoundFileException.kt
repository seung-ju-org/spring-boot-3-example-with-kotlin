package com.example.apps.files.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundFileException : RuntimeException() {
    override val message: String
        get() = "Not found file"
}
