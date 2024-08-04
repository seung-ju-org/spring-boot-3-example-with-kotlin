package com.example.aws.s3.adapters

import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.File

class S3ObjectMultipartFileAdapter(
    private var name: String? = null,
    private var originalFilename: String? = null,
    private var contentType: String? = null,
    private var content: ByteArray? = null
) : MultipartFile {
    override fun getName() = name!!

    override fun getOriginalFilename() = originalFilename

    override fun getContentType() = contentType

    override fun isEmpty() = content == null || content?.size == 0

    override fun getSize() = content!!.size.toLong()

    override fun getBytes() = content!!

    override fun getInputStream() = ByteArrayInputStream(content)

    override fun transferTo(dest: File) = throw UnsupportedOperationException("Transfer to File is not supported in this adapter.")
}
