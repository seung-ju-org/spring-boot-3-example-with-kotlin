package com.example.apps.boards.domains

import com.example.apps.users.domains.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "board_snapshots")
@EntityListeners(AuditingEntityListener::class)
class BoardSnapshot(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    var board: Board? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_category_code", nullable = false)
    var boardCategory: BoardCategory,

    @Column(name = "title", nullable = false)
    var title: String? = null,

    @Column(name = "contents", nullable = false)
    var contents: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    @CreatedBy
    var createdByUser: User? = null,

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),
)
