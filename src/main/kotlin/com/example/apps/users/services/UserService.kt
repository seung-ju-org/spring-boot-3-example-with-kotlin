package com.example.apps.users.services

import com.example.apps.files.domains.File
import com.example.apps.files.dtos.FileDto
import com.example.apps.files.repositories.FileRepository
import com.example.apps.files.services.FileService
import com.example.apps.users.domains.User
import com.example.apps.users.dtos.UserDto
import com.example.apps.users.exceptions.AlreadyExistsUserException
import com.example.apps.users.exceptions.IncorrectPasswordException
import com.example.apps.users.exceptions.NotFoundUserException
import com.example.apps.users.repositories.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val fileRepository: FileRepository,
    private val fileService: FileService
) {
    @Transactional
    fun create(create: UserDto.Create): UserDto.DetailResponse {
        if (userRepository.findAll {
            select(
                    entity(User::class)
                ).from(
                    entity(User::class)
                ).where(
                    or(
                            entity(User::class)(User::username).eq(create.username),
                            entity(User::class)(User::email).eq(create.email),
                        )
                )
        }.filterNotNull().firstOrNull() != null
        ) {
            throw AlreadyExistsUserException()
        }

        return toDetailResponse(
            userRepository.save(
                User(
                    email = create.email,
                    username = create.username,
                    firstName = create.firstName,
                    lastName = create.lastName,
                    password = passwordEncoder.encode(create.password),
                    nickname = create.nickname,
                    profileFile = create.profileFileId?.let { fileRepository.getReferenceById(create.profileFileId!!) },
                    phone = create.phone,
                )
            )
        )
    }

    fun findAll(pageable: Pageable, request: UserDto.Request): Page<UserDto.Response?> {
        return userRepository.findPage(pageable) {
            selectNew<UserDto.Response>(
                path(User::id),
                path(User::email),
                path(User::username),
                path(User::firstName),
                path(User::lastName),
                path(User::nickname),
                new(
                    FileDto.Response::class,
                    path(File::id),
                    path(File::name),
                    path(File::extension),
                    path(File::serverPath),
                    path(File::contentType),
                    path(File::size),
                    path(File::createdAt),
                ),
                path(User::phone),
                path(User::createdAt),
                path(User::updatedAt),
            ).from(
                entity(User::class),
                leftJoin(User::profileFile)
            ).where(
                and(
                    request.email?.let { entity(User::class)(User::email).like("%${request.email}%") },
                    request.username?.let { entity(User::class)(User::username).like("%${request.username}") },
                    request.firstName?.let { entity(User::class)(User::firstName).like("%${request.firstName}") },
                    request.lastName?.let { entity(User::class)(User::lastName).like("%${request.lastName}") },
                    request.nickname?.let { entity(User::class)(User::nickname).like("%${request.nickname}") },
                    request.phone?.let { entity(User::class)(User::phone).like("%${request.phone}") }
                )
            )
        }
    }

    fun findById(id: Long) = userRepository.findAll {
        selectNew<UserDto.DetailResponse>(
            path(User::id),
            path(User::email),
            path(User::username),
            path(User::firstName),
            path(User::lastName),
            path(User::nickname),
            new(
                FileDto.Response::class,
                path(File::id),
                path(File::name),
                path(File::extension),
                path(File::serverPath),
                path(File::contentType),
                path(File::size),
                path(File::createdAt),
            ),
            path(User::phone),
            path(User::createdAt),
            path(User::updatedAt),
        ).from(
            entity(User::class),
            leftJoin(User::profileFile)
        ).where(
            entity(User::class)(User::id).eq(id)
        )
    }.filterNotNull().firstOrNull() ?: throw NotFoundUserException()

    fun findByUsername(username: String) = userRepository.findAll {
        selectNew<UserDto.DetailResponse>(
            path(User::id),
            path(User::email),
            path(User::username),
            path(User::firstName),
            path(User::lastName),
            path(User::nickname),
            new(
                FileDto.Response::class,
                path(File::id),
                path(File::name),
                path(File::extension),
                path(File::serverPath),
                path(File::contentType),
                path(File::size),
                path(File::createdAt),
            ),
            path(User::phone),
            path(User::createdAt),
            path(User::updatedAt),
        ).from(
            entity(User::class),
            leftJoin(User::profileFile)
        ).where(
            entity(User::class)(User::username).eq(username)
        )
    }.filterNotNull().firstOrNull() ?: throw NotFoundUserException()

    fun findByUsernameAndPassword(username: String, password: String): UserDto.DetailResponse {
        val user = userRepository.findAll {
            select(
                entity(User::class)
            ).from(
                entity(User::class)
            ).where(
                entity(User::class)(User::username).eq(username)
            )
        }.filterNotNull().firstOrNull() ?: throw NotFoundUserException()

        if (!passwordEncoder.matches(password, user.password)) {
            throw IncorrectPasswordException()
        }

        return toDetailResponse(user)
    }

    @Transactional
    fun updateById(id: Long, update: UserDto.Update): UserDto.DetailResponse {
        var user = userRepository.findAll {
            select(
                entity(User::class)
            ).from(
                entity(User::class)
            ).where(
                entity(User::class)(User::id).eq(id)
            )
        }.filterNotNull().firstOrNull() ?: throw NotFoundUserException()

        update.firstName?.let { user.firstName = update.firstName }
        update.lastName?.let { user.lastName = update.lastName }
        update.password?.let { user.password = passwordEncoder.encode(update.password) }
        update.nickname?.let { user.nickname = update.nickname }
        update.profileFileId?.let { user.profileFile = fileRepository.getReferenceById(update.profileFileId!!) }
        update.phone?.let { user.phone = update.phone }
        user = userRepository.save(user)

        return toDetailResponse(user)
    }

    @Transactional
    fun deleteByUser(user: User) = userRepository.delete(user)

    @Transactional
    fun deleteByUserId(id: Long) = deleteByUser(
        userRepository.findAll {
            select(
                entity(User::class)
            ).from(
                entity(User::class)
            ).where(
                entity(User::class)(User::id).eq(id)
            )
        }.filterNotNull().firstOrNull() ?: throw NotFoundUserException()
    )

    @Transactional
    fun deleteAllByUsers(users: Iterable<User>) = userRepository.deleteAll(users)

    @Transactional
    fun deleteAllByUsersId(ids: Iterable<Long>) = deleteAllByUsers(
        userRepository.findAll {
            select(
                entity(User::class)
            ).from(
                entity(User::class)
            ).where(
                entity(User::class)(User::id).`in`(ids)
            )
        }.filterNotNull()
    )

    fun toDetailResponse(user: User): UserDto.DetailResponse {
        return UserDto.DetailResponse(
            id = user.id,
            email = user.email,
            username = user.username!!,
            firstName = user.firstName,
            lastName = user.lastName,
            nickname = user.nickname,
            profileFile = user.profileFile?.let { fileService.toResponse(it) },
            phone = user.phone,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
    }
}
