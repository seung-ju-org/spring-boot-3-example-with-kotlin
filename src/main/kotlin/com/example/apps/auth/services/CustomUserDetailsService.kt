package com.example.apps.auth.services

import com.example.apps.auth.domains.CustomUserDetails
import com.example.apps.users.domains.User
import com.example.apps.users.exceptions.NotFoundUserException
import com.example.apps.users.repositories.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        return CustomUserDetails(
            userRepository.findAll {
                select(
                    entity(User::class)
                ).from(
                    entity(User::class)
                ).where(
                    entity(User::class)(User::username).eq(username)
                )
            }.filterNotNull().firstOrNull() ?: throw NotFoundUserException()
        )
    }
}
