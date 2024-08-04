package com.example.apps.files.dtos

import java.time.LocalDateTime

class FileDto {
    data class Response(
        var id: Long? = null,
        var name: String? = null,
        var extension: String? = null,
        var serverPath: String? = null,
        var contentType: String? = null,
        var size: Long? = null,
        var createdAt: LocalDateTime? = null
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
