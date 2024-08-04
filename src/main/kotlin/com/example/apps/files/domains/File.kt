package com.example.apps.files.domains

import com.example.apps.users.domains.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime

@Entity
@Table(name = "files")
class File(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    var id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: Status = Status.READY,

    @Size(max = 255)
    @Column(name = "upload_id")
    var uploadId: String? = null,

    @Size(max = 255)
    @Column(name = "name", nullable = false)
    var name: String? = null,

    @Size(max = 20)
    @Column(name = "extension", nullable = false, length = 20)
    var extension: String? = null,

    @Size(max = 255)
    @Column(name = "server_path", nullable = false)
    var serverPath: String? = null,

    @Size(max = 255)
    @Column(name = "content_type", nullable = false)
    var contentType: String? = null,

    @Column(name = "size", columnDefinition = "BIGINT", nullable = false)
    var size: Long? = null,

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    var createdAt: LocalDateTime? = LocalDateTime.now(),

    @OneToMany(fetch = FetchType.LAZY)
    var users: MutableSet<User> = mutableSetOf()
) {
    enum class Status {
        READY, PROGRESS, DONE, ERROR,
    }
}
