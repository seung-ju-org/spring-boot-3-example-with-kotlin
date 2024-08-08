package com.example.apps.boards.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundBoardCategoryException : RuntimeException() {
    override val message: String
        get() = "Not found board category"
}
