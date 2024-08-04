package com.example.apps.files.dtos

import java.time.LocalDateTime

class FileDto {
    data class Response(
        var id: Long,
        var name: String,
        var extension: String,
        var serverPath: String,
        var contentType: String,
        var size: Long,
        var createdAt: LocalDateTime
    )

    data class CreateMultipartUpload(
        var name: String,
        var contentType: String,
        var size: Long
    )

    data class CreateMultipartUploadResponse(
        var id: Long
    )

    data class UploadPartResponse(
        var etag: String
    )

    data class CompleteMultipartUploadPart(
        var etag: String,
        var partNumber: Int
    )

    data class CompleteMultipartUpload(
        var parts: List<CompleteMultipartUploadPart>
    )
}
