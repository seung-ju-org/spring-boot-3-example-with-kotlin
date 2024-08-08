package com.example.apps.users.domains

import com.example.apps.files.domains.File
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener::class)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_file_id")
    var profileFile: File? = null,

    @Column(name = "phone")
    var phone: String? = null,

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    var accounts: Set<Account> = emptySet(),
)
