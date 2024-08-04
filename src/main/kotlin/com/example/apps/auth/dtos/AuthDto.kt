package com.example.apps.auth.dtos

class AuthDto {
    data class Join(
        var email: String? = null,
        var username: String,
        var firstName: String? = null,
        var lastName: String? = null,
        var password: String? = null,
        var nickname: String? = null,
        var profileFileId: Long? = null,
        var phone: String? = null
    )

    data class Login(
        var username: String,
        var password: String
    )

    data class Logout(
        var grantType: String,
        var jti: String,
        var refreshToken: String
    )

    data class OauthToken(
        var grantType: String,
        var jti: String,
        var refreshToken: String
    )
}
