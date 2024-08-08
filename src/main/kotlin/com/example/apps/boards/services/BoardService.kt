package com.example.apps.boards.services

import com.example.apps.boards.domains.Board
import com.example.apps.boards.domains.BoardCategory
import com.example.apps.boards.domains.BoardCategorySnapshot
import com.example.apps.boards.domains.BoardSnapshot
import com.example.apps.boards.dtos.BoardCategoryDto
import com.example.apps.boards.dtos.BoardDto
import com.example.apps.boards.exceptions.NotFoundBoardException
import com.example.apps.boards.repositories.BoardCategoryRepository
import com.example.apps.boards.repositories.BoardRepository
import com.example.apps.boards.repositories.BoardSnapshotRepository
import com.example.apps.files.domains.File
import com.example.apps.files.dtos.FileDto
import com.example.apps.users.domains.User
import com.example.apps.users.dtos.UserDto
import com.example.apps.users.services.UserService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class BoardService(
    private val boardRepository: BoardRepository,
    private val boardSnapshotRepository: BoardSnapshotRepository,
    private val boardCategoryRepository: BoardCategoryRepository,
    private val userService: UserService,
    private val boardCategoryService: BoardCategoryService
) {
    @Transactional
    fun create(create: BoardDto.Create): BoardDto.DetailResponse {
        var board = boardRepository.save(Board())
        board.boardSnapshot = boardSnapshotRepository.save(
            BoardSnapshot(
                board = board,
                boardCategory = boardCategoryRepository.getReferenceById(create.boardCategoryId!!),
                title = create.title,
                contents = create.contents
            )
        )
        board = boardRepository.save(board)
        return toDetailResponse(board)
    }

    @Transactional
    fun findAll(pageable: Pageable, request: BoardDto.Request) = boardRepository.findPage(pageable) {
        selectNew<BoardDto.Response>(
            path(Board::id),
            new(
                BoardCategoryDto.SimpleResponse::class,
                path(BoardCategory::id),
                path(BoardCategory::boardCategorySnapshot)(BoardCategorySnapshot::name)
            ),
            path(BoardSnapshot::title),
            new(
                UserDto.SimpleResponse::class,
                path(Board::createdByUser)(User::id),
                path(Board::createdByUser)(User::nickname),
                new(
                    FileDto.SimpleResponse::class,
                    path(Board::createdByUser)(User::profileFile)(File::id),
                    path(Board::createdByUser)(User::profileFile)(File::name),
                    path(Board::createdByUser)(User::profileFile)(File::extension),
                    path(Board::createdByUser)(User::profileFile)(File::contentType),
                    path(Board::createdByUser)(User::profileFile)(File::size),
                ),
            ),
            new(
                UserDto.SimpleResponse::class,
                path(Board::updatedByUser)(User::id),
                path(Board::updatedByUser)(User::nickname),
                new(
                    FileDto.SimpleResponse::class,
                    path(Board::updatedByUser)(User::profileFile)(File::id),
                    path(Board::updatedByUser)(User::profileFile)(File::name),
                    path(Board::updatedByUser)(User::profileFile)(File::extension),
                    path(Board::updatedByUser)(User::profileFile)(File::contentType),
                    path(Board::updatedByUser)(User::profileFile)(File::size),
                ),
            ),
            path(Board::createdAt),
            path(Board::updatedAt),
        ).from(
            entity(Board::class),
            leftJoin(Board::boardSnapshot),
            leftJoin(BoardCategory::class).on(path(BoardSnapshot::boardCategory)(BoardCategory::id).eq(path(BoardCategory::id))),
            leftJoin(Board::createdByUser).alias(entity(User::class, "CreatedByUser")),
            leftJoin(path(Board::createdByUser)(User::profileFile)).alias(entity(File::class, "CreatedByUserProfileFile")),
            leftJoin(Board::updatedByUser).alias(entity(User::class, "UpdatedByUser")),
            leftJoin(path(Board::updatedByUser)(User::profileFile)).alias(entity(File::class, "UpdatedByUserProfileFile"))
        ).where(
            and(
                entity(Board::class)(Board::deletedAt).isNull(),
                request.boardCategoryId?.let {
                    entity(Board::class)(Board::boardSnapshot)(BoardSnapshot::boardCategory)(BoardCategory::id).eq(
                        it
                    )
                },
                request.title?.let {
                    entity(Board::class)(Board::boardSnapshot)(BoardSnapshot::title).like("%$it%")
                }
            )
        )
    }

    @Transactional
    fun findById(id: Long) = boardRepository.findAll {
        selectNew<BoardDto.DetailResponse>(
            path(Board::id),
            new(
                BoardCategoryDto.SimpleResponse::class,
                path(BoardCategory::id),
                path(BoardCategory::boardCategorySnapshot)(BoardCategorySnapshot::name)
            ),
            path(BoardSnapshot::title),
            path(BoardSnapshot::contents),
            new(
                UserDto.SimpleResponse::class,
                path(Board::createdByUser)(User::id),
                path(Board::createdByUser)(User::nickname),
                new(
                    FileDto.SimpleResponse::class,
                    path(Board::createdByUser)(User::profileFile)(File::id),
                    path(Board::createdByUser)(User::profileFile)(File::name),
                    path(Board::createdByUser)(User::profileFile)(File::extension),
                    path(Board::createdByUser)(User::profileFile)(File::contentType),
                    path(Board::createdByUser)(User::profileFile)(File::size),
                ),
            ),
            new(
                UserDto.SimpleResponse::class,
                path(Board::updatedByUser)(User::id),
                path(Board::updatedByUser)(User::nickname),
                new(
                    FileDto.SimpleResponse::class,
                    path(Board::updatedByUser)(User::profileFile)(File::id),
                    path(Board::updatedByUser)(User::profileFile)(File::name),
                    path(Board::updatedByUser)(User::profileFile)(File::extension),
                    path(Board::updatedByUser)(User::profileFile)(File::contentType),
                    path(Board::updatedByUser)(User::profileFile)(File::size),
                ),
            ),
            path(Board::createdAt),
            path(Board::updatedAt),
        ).from(
            entity(Board::class),
            leftJoin(Board::boardSnapshot),
            leftJoin(BoardCategory::class).on(path(BoardSnapshot::boardCategory)(BoardCategory::id).eq(path(BoardCategory::id))),
            leftJoin(Board::createdByUser).alias(entity(User::class, "CreatedByUser")),
            leftJoin(path(Board::createdByUser)(User::profileFile)).alias(entity(File::class, "CreatedByUserProfileFile")),
            leftJoin(Board::updatedByUser).alias(entity(User::class, "UpdatedByUser")),
            leftJoin(path(Board::updatedByUser)(User::profileFile)).alias(entity(File::class, "UpdatedByUserProfileFile"))
        ).where(
            and(
                entity(Board::class)(Board::id).eq(id),
                entity(Board::class)(Board::deletedAt).isNull()
            )
        )
    }.filterNotNull().firstOrNull() ?: throw NotFoundBoardException()

    @Transactional
    fun update(id: Long, update: BoardDto.Update): BoardDto.DetailResponse {
        var board = boardRepository.findAll {
            select(
                entity(Board::class)
            ).from(
                entity(Board::class)
            ).where(
                and(
                    entity(Board::class)(Board::id).eq(id),
                    entity(Board::class)(Board::deletedAt).isNull()
                )
            )
        }.filterNotNull().firstOrNull() ?: throw NotFoundBoardException()

        board.boardSnapshot = boardSnapshotRepository.save(
            BoardSnapshot(
                board = board,
                boardCategory = boardCategoryRepository.getReferenceById(update.boardCategoryId!!),
                title = update.title,
                contents = update.contents
            )
        )

        board = boardRepository.save(board)

        return toDetailResponse(board)
    }

    @Transactional
    fun deleteById(id: Long) {
        var board = boardRepository.findAll {
            select(
                entity(Board::class)
            ).from(
                entity(Board::class)
            ).where(
                and(
                    entity(Board::class)(Board::id).eq(id),
                    entity(Board::class)(Board::deletedAt).isNull()
                )
            )
        }.filterNotNull().firstOrNull() ?: throw NotFoundBoardException()

        board.deletedAt = LocalDateTime.now()

        board = boardRepository.save(board)
    }

    fun toDetailResponse(board: Board): BoardDto.DetailResponse {
        return BoardDto.DetailResponse(
            id = board.id,
            boardCategory = boardCategoryService.toSimpleResponse(board.boardSnapshot?.boardCategory!!),
            title = board.boardSnapshot?.title,
            contents = board.boardSnapshot?.contents,
            createdByUser = userService.toSimpleResponse(board.createdByUser!!),
            updatedByUser = userService.toSimpleResponse(board.updatedByUser!!),
            createdAt = board.createdAt,
            updatedAt = board.updatedAt,
        )
    }
}
