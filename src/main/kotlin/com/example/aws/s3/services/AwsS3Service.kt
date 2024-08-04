package com.example.aws.s3.services

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.CompleteMultipartUploadRequest
import aws.sdk.kotlin.services.s3.model.CompletedMultipartUpload
import aws.sdk.kotlin.services.s3.model.CompletedPart
import aws.sdk.kotlin.services.s3.model.CreateMultipartUploadRequest
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.GetObjectResponse
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.model.UploadPartRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

@Service
class AwsS3Service(
    private val s3Client: S3Client
) {
    @Value("\${cloud.aws.s3.bucket}")
    lateinit var bucketName: String

    suspend fun uploadFile(multipartFile: MultipartFile, path: Path) = uploadFile("files", multipartFile, path)

    suspend fun uploadFile(objectKey: String, multipartFile: MultipartFile, path: Path) =
        s3Client.putObject(
            PutObjectRequest {
                bucket = bucketName
                key = "$objectKey/${path.parent}/${path.fileName}"
                contentType = multipartFile.contentType
                contentLength = multipartFile.size
                body = ByteStream.fromBytes(multipartFile.bytes)
            }
        )

    suspend fun uploadFile(bytes: ByteArray, path: Path) = uploadFile("files", bytes, path)

    suspend fun uploadFile(objectKey: String, bytes: ByteArray, path: Path) =
        s3Client.putObject(
            PutObjectRequest {
                bucket = bucketName
                key = "$objectKey/${path.parent}/${path.fileName}"
                body = ByteStream.fromBytes(bytes)
            }
        )

    suspend fun uploadUnzippedFilesToS3(objectKey: String, zipFileBytes: ByteArray, path: Path) {
        val inputStream = ByteArrayInputStream(zipFileBytes)
        val zipInputStream = ZipInputStream(inputStream)

        val buffer = ByteArray(1024)
        var entry: ZipEntry
        while ((zipInputStream.nextEntry.also { entry = it }) != null) {
            var length: Int
            val outputStream = ByteArrayOutputStream()
            while ((zipInputStream.read(buffer).also { length = it }) > 0) {
                outputStream.write(buffer, 0, length)
            }
            s3Client.putObject(
                PutObjectRequest {
                    bucket = bucketName
                    key = "$objectKey/${path.parent}/${path.fileName}/${entry.name}"
                    body = ByteStream.fromBytes(outputStream.toByteArray())
                }
            )
        }
    }

    suspend fun <T> getFile(path: Path, block: suspend ((GetObjectResponse) -> T)) =
        getFile("files", path, block)

    suspend fun <T> getFile(
        objectKey: String,
        path: Path,
        block: suspend ((GetObjectResponse) -> T)
    ) = s3Client.getObject(
        GetObjectRequest {
            bucket = bucketName
            key = "$objectKey/${path.parent}/${path.fileName}"
        },
        block
    )

    suspend fun createMultipartUpload(path: Path) = createMultipartUpload("files", path)

    suspend fun createMultipartUpload(objectKey: String, path: Path) =
        s3Client.createMultipartUpload(
            CreateMultipartUploadRequest {
                bucket = bucketName
                key = "$objectKey/${path.parent}/${path.fileName}"
            }
        )

    suspend fun uploadPart(path: Path, objectUploadId: String, multipartFile: MultipartFile, objectPartNumber: Int) =
        uploadPart("files", path, objectUploadId, multipartFile, objectPartNumber)

    suspend fun uploadPart(
        objectKey: String,
        path: Path,
        objectUploadId: String,
        multipartFile: MultipartFile,
        objectPartNumber: Int
    ) =
        s3Client.uploadPart(
            UploadPartRequest {
                bucket = bucketName
                key = "$objectKey/${path.parent}/${path.fileName}"
                uploadId = objectUploadId
                partNumber = objectPartNumber
                body = ByteStream.fromBytes(multipartFile.bytes)
            }
        )

    suspend fun completeMultipartUpload(path: Path, objectUploadId: String, completedParts: List<CompletedPart>) =
        completeMultipartUpload("files", path, objectUploadId, completedParts)

    suspend fun completeMultipartUpload(
        objectKey: String,
        path: Path,
        objectUploadId: String,
        completedParts: List<CompletedPart>
    ) =
        s3Client.completeMultipartUpload(
            CompleteMultipartUploadRequest {
                bucket = bucketName
                key = "$objectKey/${path.parent}/${path.fileName}"
                uploadId = objectUploadId
                multipartUpload = CompletedMultipartUpload {
                    parts = completedParts
                }
            }
        )
}
