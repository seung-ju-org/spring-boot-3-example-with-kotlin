package com.example.apps.files.services

import aws.sdk.kotlin.services.s3.model.CompletedPart
import com.example.apps.files.domains.File
import com.example.apps.files.dtos.FileDto
import com.example.apps.files.dtos.FileDto.CreateMultipartUpload
import com.example.apps.files.exceptions.NotFoundFileException
import com.example.apps.files.exceptions.UnablePartCompleteException
import com.example.apps.files.exceptions.UnablePartUploadException
import com.example.apps.files.repositories.FileRepository
import com.example.aws.s3.services.AwsS3Service
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.io.FilenameUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
@Transactional(readOnly = true)
class FileService(
    private val fileRepository: FileRepository,
    private val awsS3Service: AwsS3Service
) {
    fun get(id: Long) = fileRepository.findAll {
        selectNew<FileDto.Response>(
            path(File::id),
            path(File::name),
            path(File::extension),
            path(File::serverPath),
            path(File::contentType),
            path(File::size),
            path(File::createdAt)
        ).from(
            entity(File::class)
        ).where(
            and(
                entity(File::class)(File::id).eq(id),
                entity(File::class)(File::status).eq(File.Status.DONE)
            )
        )
    }.filterNotNull().firstOrNull() ?: throw NotFoundFileException()

    @Transactional
    suspend fun upload(multipartFile: MultipartFile): FileDto.Response {
        val now = LocalDateTime.now()
        var file = File(
            name = multipartFile.originalFilename,
            extension = FilenameUtils.getExtension(multipartFile.originalFilename),
            serverPath = now.format(DateTimeFormatter.ofPattern("yyyy/MM")),
            contentType = multipartFile.contentType,
            size = multipartFile.size
        )

        file = withContext(Dispatchers.IO) {
            fileRepository.save(file)
        }
        val path = Paths.get("${file.serverPath}/${file.id}/source")
        try {
            awsS3Service.uploadFile(multipartFile, path)
        } catch (e: Exception) {
            file.status = File.Status.ERROR
            withContext(Dispatchers.IO) {
                fileRepository.save(file)
            }
            throw RuntimeException(e)
        }

        file.status = File.Status.DONE
        file = withContext(Dispatchers.IO) {
            fileRepository.save(file)
        }

        return withContext(Dispatchers.IO) {
            toResponse(file)
        }
    }

    @Transactional
    suspend fun createMultipartUpload(createMultipartUpload: CreateMultipartUpload): FileDto.CreateMultipartUploadResponse {
        val now = LocalDateTime.now()
        var file = File(
            name = createMultipartUpload.name,
            contentType = createMultipartUpload.contentType,
            size = createMultipartUpload.size,
            serverPath = now.format(DateTimeFormatter.ofPattern("yyyy/MM")),
            extension = FilenameUtils.getExtension(createMultipartUpload.name),
            status = File.Status.READY
        )

        file = withContext(Dispatchers.IO) {
            fileRepository.save(file)
        }

        val path = Path.of("${file.serverPath}/${file.id}/source")

        val createMultipartUploadResponse = awsS3Service.createMultipartUpload(path)

        file.uploadId = createMultipartUploadResponse.uploadId

        file = withContext(Dispatchers.IO) {
            fileRepository.save(file)
        }

        return FileDto.CreateMultipartUploadResponse(
            id = file.id!!,
        )
    }

    @Transactional
    suspend fun uploadPart(id: Long, multipartFile: MultipartFile, partNumber: Int): FileDto.UploadPartResponse {
        var file = fileRepository.findAll {
            select(
                entity(File::class)
            ).from(
                entity(File::class)
            ).where(
                entity(File::class)(File::id).eq(id)
            )
        }.filterNotNull().firstOrNull() ?: throw NotFoundFileException()

        val path = Path.of("${file.serverPath}/${file.id}/source")

        if (!listOf(File.Status.READY, File.Status.PROGRESS).contains(file.status))
            throw UnablePartUploadException()

        if (file.status == File.Status.READY) {
            file.status = File.Status.PROGRESS
            file = withContext(Dispatchers.IO) {
                fileRepository.save(file)
            }
        }

        try {
            val uploadPartResponse = awsS3Service.uploadPart(path, file.uploadId!!, multipartFile, partNumber)
            return FileDto.UploadPartResponse(
                etag = uploadPartResponse.eTag!!
            )
        } catch (e: Exception) {
            file.status = File.Status.ERROR
            withContext(Dispatchers.IO) {
                fileRepository.save(file)
            }
            throw RuntimeException(e)
        }
    }

    suspend fun completeMultipartUpload(id: Long, completeMultipartUpload: FileDto.CompleteMultipartUpload): FileDto.Response {
        var file = fileRepository.findAll {
            select(
                entity(File::class)
            ).from(
                entity(File::class)
            ).where(
                entity(File::class)(File::id).eq(id)
            )
        }.filterNotNull().firstOrNull() ?: throw NotFoundFileException()

        if (file.status != File.Status.PROGRESS) throw UnablePartCompleteException()

        val path = Path.of("${file.serverPath}/${file.id}/source")

        val completedParts = completeMultipartUpload.parts.map {
            CompletedPart {
                eTag = it.etag
            }
        }

        try {
            awsS3Service.completeMultipartUpload(path, file.uploadId!!, completedParts)
            file.status = File.Status.DONE
        } catch (e: Exception) {
            file.status = File.Status.ERROR
            withContext(Dispatchers.IO) {
                fileRepository.save(file)
            }
            throw RuntimeException(e)
        }

        file = withContext(Dispatchers.IO) {
            fileRepository.save(file)
        }

        return withContext(Dispatchers.IO) {
            toResponse(file)
        }
    }

    fun toResponse(file: File) = FileDto.Response(
        id = file.id!!,
        name = file.name!!,
        extension = file.extension!!,
        serverPath = file.serverPath!!,
        contentType = file.contentType!!,
        size = file.size!!,
        createdAt = file.createdAt!!
    )

    fun toSimpleResponse(file: File) = FileDto.SimpleResponse(
        id = file.id!!,
        name = file.name!!,
        extension = file.extension!!,
        contentType = file.contentType!!,
        size = file.size!!,
    )
}
