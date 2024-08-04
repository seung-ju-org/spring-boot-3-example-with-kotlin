package com.example.apps.auth.domains

import com.example.apps.users.domains.User
import com.example.security.enums.Authority
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(val user: User) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        val collection: MutableCollection<GrantedAuthority> = ArrayList()
        collection.add(SimpleGrantedAuthority(Authority.ROLES.USER))
        return collection
    }

    override fun getPassword(): String? {
        return user.password
    }

    override fun getUsername(): String? {
        return user.username
    }

    override fun isAccountNonExpired(): Boolean {
        return super.isAccountNonExpired()
    }

    override fun isAccountNonLocked(): Boolean {
        return super.isAccountNonLocked()
    }

    override fun isCredentialsNonExpired(): Boolean {
        return super.isCredentialsNonExpired()
    }

    override fun isEnabled(): Boolean {
        return super.isEnabled()
    }
}
