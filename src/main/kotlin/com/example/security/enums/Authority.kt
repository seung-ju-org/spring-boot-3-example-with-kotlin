package com.example.security.enums

import org.springframework.security.core.GrantedAuthority

enum class Authority(private val authority: String) : GrantedAuthority {
    ;

    class ROLES {
        companion object {
            const val USER = "ROLE_USER"
            const val ADMIN = "ROLE_ADMIN"
        }
    }

    override fun getAuthority(): String {
        return authority
    }
}
