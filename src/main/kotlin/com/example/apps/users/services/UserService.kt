package com.example.apps.users.services

import com.example.apps.users.domains.User
import com.example.apps.users.dtos.UserDto
import com.example.apps.users.exceptions.AlreadyExistsUserException
import com.example.apps.users.exceptions.IncorrectPasswordException
import com.example.apps.users.exceptions.NotFoundUserException
import com.example.apps.users.repositories.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
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
                    profile = create.profile,
                    phone = create.phone,
                )
            )
        )
    }

    fun findAll() = userRepository.findAll {
        selectNew<UserDto.Response>(
            path(User::id),
            path(User::email),
            path(User::username),
            path(User::firstName),
            path(User::lastName),
            path(User::nickname),
            path(User::profile),
            path(User::phone),
            path(User::createdAt),
            path(User::updatedAt),
        ).from(
            entity(User::class)
        )
    }.filterNotNull()

    fun findById(id: Long) = userRepository.findAll {
        selectNew<UserDto.DetailResponse>(
            path(User::id),
            path(User::email),
            path(User::username),
            path(User::firstName),
            path(User::lastName),
            path(User::nickname),
            path(User::profile),
            path(User::phone),
            path(User::createdAt),
            path(User::updatedAt),
        ).from(
            entity(User::class)
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
            path(User::profile),
            path(User::phone),
            path(User::createdAt),
            path(User::updatedAt),
        ).from(
            entity(User::class)
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

        if (update.firstName != null) user.firstName = update.firstName
        if (update.lastName != null) user.lastName = update.lastName
        if (update.password != null) user.password = passwordEncoder.encode(update.password)
        if (update.nickname != null) user.nickname = update.nickname
        if (update.profile != null) user.profile = update.profile
        if (update.phone != null) user.phone = update.phone
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
            profile = user.profile,
            phone = user.phone,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
    }
}
