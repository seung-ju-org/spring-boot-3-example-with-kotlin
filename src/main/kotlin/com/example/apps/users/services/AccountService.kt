package com.example.apps.users.services

import com.example.apps.users.domains.Account
import com.example.apps.users.exceptions.NotFoundAccountException
import com.example.apps.users.repositories.AccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AccountService(
    private val accountRepository: AccountRepository
) {
    fun get(id: Long) = accountRepository.findAll {
        select(
            entity(Account::class, "a")
        ).from(
            entity(Account::class, "a")
        ).where(
            entity(Account::class, "a")(Account::id).eq(id)
        )
    }.filterNotNull().firstOrNull() ?: throw NotFoundAccountException()
}
