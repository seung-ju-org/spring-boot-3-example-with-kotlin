package com.example.apps.auth.services

import com.example.apps.auth.constants.AuthConstants
import com.example.apps.auth.dtos.AuthDto
import com.example.apps.auth.dtos.JwtDto
import com.example.apps.auth.exceptions.InvalidGrantTypeException
import com.example.apps.users.dtos.UserDto
import com.example.apps.users.services.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthService(
    private val userService: UserService,
    private val jwtService: JwtService
) {
    fun me(userId: Long) = userService.findById(userId)

    fun updateMe(userId: Long, update: UserDto.Update) = userService.updateById(userId, update)

    @Transactional
    fun deleteMe(userId: Long) = userService.deleteByUserId(userId)

    fun sessions(userId: Long) = jwtService.findAllByUserId(userId)

    @Transactional
    fun join(join: AuthDto.Join) = userService.create(
        UserDto.Create(
            email = join.email,
            username = join.username,
            firstName = join.firstName,
            lastName = join.lastName,
            password = join.password,
            nickname = join.nickname,
            profileFileId = join.profileFileId,
            phone = join.phone,
        )
    )

    fun login(login: AuthDto.Login): JwtDto.Response {
        val user = userService.findByUsernameAndPassword(login.username, login.password)
        return jwtService.createJwt(user.id!!)
    }

    fun logout(userId: Long, logout: AuthDto.Logout) {
        when (logout.grantType) {
            AuthConstants.JWT_TOKEN_TYPE -> return jwtService.expireToken(logout.jti, logout.refreshToken, userId)
            else -> throw InvalidGrantTypeException()
        }
    }

    fun logoutAll(userId: Long) = jwtService.expireTokensByUserId(userId)

    fun oauthToken(oauthToken: AuthDto.OauthToken): JwtDto.Response {
        when (oauthToken.grantType) {
            AuthConstants.JWT_TOKEN_TYPE -> return jwtService.renewalToken(oauthToken.jti, oauthToken.refreshToken)
            else -> throw InvalidGrantTypeException()
        }
    }
}
