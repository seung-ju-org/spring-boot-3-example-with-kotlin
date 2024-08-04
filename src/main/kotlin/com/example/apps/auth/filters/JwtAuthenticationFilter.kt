package com.example.apps.auth.filters

import com.example.apps.auth.constants.AuthConstants
import com.example.apps.auth.domains.CustomUserDetails
import com.example.apps.auth.services.JwtService
import com.example.apps.users.domains.User
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val authHeader = request.getHeader(AuthConstants.AUTH_HEADER)

        if (
            SecurityContextHolder.getContext().authentication == null &&
            authHeader != null &&
            authHeader.lowercase().startsWith(AuthConstants.JWT_TOKEN_TYPE.lowercase()) &&
            authHeader.length > AuthConstants.JWT_TOKEN_TYPE.length
        ) {
            val accessToken = authHeader.substring(AuthConstants.JWT_TOKEN_TYPE.length + 1)
            val id = jwtService.extractId(accessToken)
            val userDetails = CustomUserDetails(
                User(
                    id = id
                )
            )
            val authentication = UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.password,
                userDetails.authorities
            )
            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }
}
