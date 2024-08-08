package com.example.apps.boards.services

import com.example.apps.boards.domains.BoardCategory
import com.example.apps.boards.domains.BoardCategorySnapshot
import com.example.apps.boards.dtos.BoardCategoryDto
import com.example.apps.boards.exceptions.NotFoundBoardCategoryException
import com.example.apps.boards.repositories.BoardCategoryRepository
import com.example.apps.boards.repositories.BoardCategorySnapshotRepository
import com.example.apps.files.domains.File
import com.example.apps.files.dtos.FileDto
import com.example.apps.users.domains.User
import com.example.apps.users.dtos.UserDto
import com.example.apps.users.services.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class BoardCategoryService(
    private val boardCategoryRepository: BoardCategoryRepository,
    private val boardCategorySnapshotRepository: BoardCategorySnapshotRepository,
    private val userService: UserService
) {
    @Transactional
    fun create(create: BoardCategoryDto.Create): BoardCategoryDto.DetailResponse {
        var boardCategory = boardCategoryRepository.save(BoardCategory())
        boardCategory.boardCategorySnapshot = boardCategorySnapshotRepository.save(
            BoardCategorySnapshot(
                boardCategory = boardCategory,
                name = create.name
            )
        )
        boardCategory = boardCategoryRepository.save(boardCategory)
        return toDetailResponse(boardCategory)
    }

    fun findAll() = boardCategoryRepository.findAll {
        selectNew<BoardCategoryDto.Response>(
            path(BoardCategory::id),
            path(BoardCategory::boardCategorySnapshot)(BoardCategorySnapshot::name),
            new(
                UserDto.SimpleResponse::class,
                path(BoardCategory::createdByUser)(User::id),
                path(BoardCategory::createdByUser)(User::nickname),
                new(
                    FileDto.SimpleResponse::class,
                    path(BoardCategory::createdByUser)(User::profileFile)(File::id),
                    path(BoardCategory::createdByUser)(User::profileFile)(File::name),
                    path(BoardCategory::createdByUser)(User::profileFile)(File::extension),
                    path(BoardCategory::createdByUser)(User::profileFile)(File::contentType),
                    path(BoardCategory::createdByUser)(User::profileFile)(File::size),
                ),
            ),
            new(
                UserDto.SimpleResponse::class,
                path(BoardCategory::updatedByUser)(User::id),
                path(BoardCategory::updatedByUser)(User::nickname),
                new(
                    FileDto.SimpleResponse::class,
                    path(BoardCategory::updatedByUser)(User::profileFile)(File::id),
                    path(BoardCategory::updatedByUser)(User::profileFile)(File::name),
                    path(BoardCategory::updatedByUser)(User::profileFile)(File::extension),
                    path(BoardCategory::updatedByUser)(User::profileFile)(File::contentType),
                    path(BoardCategory::updatedByUser)(User::profileFile)(File::size),
                ),
            ),
            path(BoardCategory::createdAt),
            path(BoardCategory::updatedAt),
        ).from(
            entity(BoardCategory::class),
            leftJoin(BoardCategory::boardCategorySnapshot),
            leftJoin(BoardCategory::createdByUser).alias(entity(User::class, "CreatedByUser")),
            leftJoin(path(BoardCategory::createdByUser)(User::profileFile)).alias(entity(File::class, "CreatedByUserProfileFile")),
            leftJoin(BoardCategory::updatedByUser).alias(entity(User::class, "UpdatedByUser")),
            leftJoin(path(BoardCategory::updatedByUser)(User::profileFile)).alias(entity(File::class, "UpdatedByUserProfileFile"))
        ).where(
            entity(BoardCategory::class)(BoardCategory::deletedAt).isNull()
        )
    }

    fun findById(id: Long) = boardCategoryRepository.findAll {
        selectNew<BoardCategoryDto.Response>(
            path(BoardCategory::id),
            path(BoardCategory::boardCategorySnapshot)(BoardCategorySnapshot::name),
            new(
                UserDto.SimpleResponse::class,
                path(BoardCategory::createdByUser)(User::id),
                path(BoardCategory::createdByUser)(User::nickname),
                new(
                    FileDto.SimpleResponse::class,
                    path(BoardCategory::createdByUser)(User::profileFile)(File::id),
                    path(BoardCategory::createdByUser)(User::profileFile)(File::name),
                    path(BoardCategory::createdByUser)(User::profileFile)(File::extension),
                    path(BoardCategory::createdByUser)(User::profileFile)(File::contentType),
                    path(BoardCategory::createdByUser)(User::profileFile)(File::size),
                ),
            ),
            new(
                UserDto.SimpleResponse::class,
                path(BoardCategory::updatedByUser)(User::id),
                path(BoardCategory::updatedByUser)(User::nickname),
                new(
                    FileDto.SimpleResponse::class,
                    path(BoardCategory::updatedByUser)(User::profileFile)(File::id),
                    path(BoardCategory::updatedByUser)(User::profileFile)(File::name),
                    path(BoardCategory::updatedByUser)(User::profileFile)(File::extension),
                    path(BoardCategory::updatedByUser)(User::profileFile)(File::contentType),
                    path(BoardCategory::updatedByUser)(User::profileFile)(File::size),
                ),
            ),
            path(BoardCategory::createdAt),
            path(BoardCategory::updatedAt),
        ).from(
            entity(BoardCategory::class),
            leftJoin(BoardCategory::boardCategorySnapshot),
            leftJoin(BoardCategory::createdByUser).alias(entity(User::class, "CreatedByUser")),
            leftJoin(path(BoardCategory::createdByUser)(User::profileFile)).alias(entity(File::class, "CreatedByUserProfileFile")),
            leftJoin(BoardCategory::updatedByUser).alias(entity(User::class, "UpdatedByUser")),
            leftJoin(path(BoardCategory::updatedByUser)(User::profileFile)).alias(entity(File::class, "UpdatedByUserProfileFile"))
        ).where(
            and(
                entity(BoardCategory::class)(BoardCategory::id).eq(id),
                entity(BoardCategory::class)(BoardCategory::deletedAt).isNull()
            )
        )
    }.filterNotNull().firstOrNull() ?: throw NotFoundBoardCategoryException()

    @Transactional
    fun update(id: Long, update: BoardCategoryDto.Update): BoardCategoryDto.DetailResponse {
        var boardCategory = boardCategoryRepository.findAll {
            select(
                entity(BoardCategory::class)
            ).from(
                entity(BoardCategory::class)
            ).where(
                and(
                    entity(BoardCategory::class)(BoardCategory::id).eq(id),
                    entity(BoardCategory::class)(BoardCategory::deletedAt).isNull()
                )
            )
        }.filterNotNull().firstOrNull() ?: throw NotFoundBoardCategoryException()

        boardCategory.boardCategorySnapshot = boardCategorySnapshotRepository.save(
            BoardCategorySnapshot(
                boardCategory = boardCategory,
                name = update.name
            )
        )

        boardCategory = boardCategoryRepository.save(boardCategory)

        return toDetailResponse(boardCategory)
    }

    fun deleteById(id: Long) {
        var boardCategory = boardCategoryRepository.findAll {
            select(
                entity(BoardCategory::class)
            ).from(
                entity(BoardCategory::class)
            ).where(
                and(
                    entity(BoardCategory::class)(BoardCategory::id).eq(id),
                    entity(BoardCategory::class)(BoardCategory::deletedAt).isNull()
                )
            )
        }.filterNotNull().firstOrNull() ?: throw NotFoundBoardCategoryException()

        boardCategory.deletedAt = LocalDateTime.now()

        boardCategory = boardCategoryRepository.save(boardCategory)
    }

    fun toDetailResponse(boardCategory: BoardCategory): BoardCategoryDto.DetailResponse {
        return BoardCategoryDto.DetailResponse(
            id = boardCategory.id,
            name = boardCategory.boardCategorySnapshot?.name,
            createdByUser = userService.toSimpleResponse(boardCategory.createdByUser!!),
            updatedByUser = userService.toSimpleResponse(boardCategory.updatedByUser!!),
            createdAt = boardCategory.createdAt,
            updatedAt = boardCategory.updatedAt
        )
    }

    fun toSimpleResponse(boardCategory: BoardCategory): BoardCategoryDto.SimpleResponse {
        return BoardCategoryDto.SimpleResponse(
            id = boardCategory.id,
            name = boardCategory.boardCategorySnapshot?.name
        )
    }
}
