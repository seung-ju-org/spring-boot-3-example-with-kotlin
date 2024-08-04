package com.example.apps.users.domains

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "email", unique = true)
    var email: String? = null,

    @Column(name = "username", unique = true, nullable = false)
    var username: String? = null,

    @Column(name = "first_name")
    var firstName: String? = null,

    @Column(name = "last_name")
    var lastName: String? = null,

    @Column(name = "password")
    var password: String? = null,

    @Column(name = "nickname")
    var nickname: String? = null,

    @Column(name = "profile")
    var profile: String? = null,

    @Column(name = "phone")
    var phone: String? = null,

    @Column(name = "created_at")
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    var accounts: Set<Account> = emptySet(),
)
