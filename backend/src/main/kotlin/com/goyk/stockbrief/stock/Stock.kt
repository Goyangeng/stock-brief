package com.goyk.stockbrief.stock

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "stocks")
@EntityListeners(AuditingEntityListener::class)
class Stock(
    @Column(nullable = false, unique = true, length = 20)
    var ticker: String,

    @Column(nullable = false, length = 100)
    var name: String,

    @Column(nullable = false, length = 20)
    var market: String,

    @Column(columnDefinition = "TEXT")
    var memo: String? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant? = null,

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant? = null,
)
