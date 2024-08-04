package com.example.apps.files.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.FORBIDDEN)
class UnablePartUploadException : RuntimeException() {
    override val message: String
        get() = "Unable to upload PartUpload"
}
