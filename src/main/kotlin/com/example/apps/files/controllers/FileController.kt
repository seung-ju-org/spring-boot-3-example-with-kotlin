package com.example.apps.files.controllers

import aws.smithy.kotlin.runtime.content.toByteArray
import com.example.apps.files.dtos.FileDto
import com.example.apps.files.services.FileService
import com.example.aws.s3.services.AwsS3Service
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path

@RestController
@RequestMapping("/api/files")
@Tag(name = "File", description = "File API")
class FileController(
    private val fileService: FileService,
    private val awsS3Service: AwsS3Service
) {
    @GetMapping("/{id}")
    @Operation(summary = "Get file", description = "Get file")
    suspend fun get(@PathVariable("id") id: Long): ResponseEntity<ByteArray> {
        val fileResponse: FileDto.Response = withContext(Dispatchers.IO) {
            fileService.get(id)
        }
        val httpHeaders = HttpHeaders()
        var byteArray: ByteArray? = null
        awsS3Service.getFile(Path.of("${fileResponse.serverPath}/${fileResponse.id}/source")) {
            byteArray = it.body?.toByteArray()
            httpHeaders.contentType = MediaType.parseMediaType(it.contentType!!)
            httpHeaders.contentLength = it.contentLength!!
        }
        return ResponseEntity(byteArray, httpHeaders, HttpStatus.OK)
    }

    @GetMapping("/{id}/infos")
    @Operation(summary = "Get file info", description = "Get file info")
    fun getInfo(@PathVariable("id") id: Long) = fileService.get(id)

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "Upload file", description = "Upload file")
    suspend fun upload(@RequestPart("file") multipartFile: MultipartFile) = fileService.upload(multipartFile)

    @PostMapping("/create-multipart-upload")
    @Operation(summary = "Create multipart upload", description = "Create multipart upload")
    suspend fun createMultipartUpload(@RequestBody createMultipartUpload: FileDto.CreateMultipartUpload) =
        fileService.createMultipartUpload(createMultipartUpload)

    @PostMapping("/{id}/upload-part")
    @Operation(summary = "Upload part", description = "Upload part")
    suspend fun uploadPart(
        @PathVariable("id") id: Long,
        @RequestPart("file") multipartFile: MultipartFile,
        partNumber: Int
    ) = fileService.uploadPart(id, multipartFile, partNumber)

    @PostMapping("/{id}/complete-multipart-upload")
    @Operation(summary = "Complete multipart upload", description = "Complete multipart upload")
    suspend fun uploadPart(
        @PathVariable("id") id: Long,
        @RequestBody request: FileDto.CompleteMultipartUpload
    ) = fileService.completeMultipartUpload(id, request)
}
