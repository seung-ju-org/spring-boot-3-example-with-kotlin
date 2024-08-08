package com.example.apps.files.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.FORBIDDEN)
class UnablePartCompleteException : RuntimeException("Unable to complete PartUpload")
