package com.example.apps.auth.controllers

import com.example.apps.auth.domains.CustomUserDetails
import com.example.apps.auth.dtos.AuthDto
import com.example.apps.auth.services.AuthService
import com.example.apps.users.dtos.UserDto
import com.example.security.enums.Authority
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Auth API")
class AuthController(
    private val authService: AuthService
) {
    @GetMapping("/me")
    @Operation(summary = "Get me", description = "Get me")
    @Secured(Authority.ROLES.USER)
    fun me(@AuthenticationPrincipal user: CustomUserDetails) = authService.me(user.user.id!!)

    @PatchMapping("/me")
    @Operation(summary = "Update me", description = "Update me")
    @Secured(Authority.ROLES.USER)
    fun updateMe(
        @AuthenticationPrincipal user: CustomUserDetails,
        @RequestBody update: UserDto.Update
    ) = authService.updateMe(user.user.id!!, update)

    @DeleteMapping("/me")
    @Operation(summary = "Delete me", description = "Delete me")
    @Secured(Authority.ROLES.USER)
    fun deleteMe(@AuthenticationPrincipal user: CustomUserDetails) = authService.deleteMe(user.user.id!!)

    @GetMapping("/sessions")
    @Operation(summary = "Get sessions", description = "Get sessions")
    @Secured(Authority.ROLES.USER)
    fun sessions(@AuthenticationPrincipal user: CustomUserDetails) = authService.sessions(user.user.id!!)

    @PostMapping("/join")
    @Operation(summary = "Join", description = "Join users to join")
    fun join(@RequestBody join: AuthDto.Join) = authService.join(join)

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Login user")
    fun login(@RequestBody login: AuthDto.Login) = authService.login(login)

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Logout user")
    @Secured(Authority.ROLES.USER)
    fun logout(
        @AuthenticationPrincipal user: CustomUserDetails,
        @RequestBody logout: AuthDto.Logout
    ) = authService.logout(user.user.id!!, logout)

    @PostMapping("/logout/all")
    @Operation(summary = "Logout all", description = "Logout all user")
    @Secured(Authority.ROLES.USER)
    fun logoutAll(
        @AuthenticationPrincipal user: CustomUserDetails,
    ) = authService.logoutAll(user.user.id!!)

    @PostMapping("/oauth/token")
    @Operation(summary = "Oauth token", description = "Oauth Token")
    fun oauthToken(@RequestBody oauthToken: AuthDto.OauthToken) = authService.oauthToken(oauthToken)
}
