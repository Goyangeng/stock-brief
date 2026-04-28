package com.goyk.stockbrief.market

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "market_indices")
@EntityListeners(AuditingEntityListener::class)
class MarketIndex(
    @Column(nullable = false, length = 20)
    var symbol: String,

    @Column(nullable = false, length = 100)
    var name: String,

    @Column(nullable = false)
    var date: LocalDate,

    @Column(name = "closing_price", nullable = false, precision = 15, scale = 4)
    var closingPrice: BigDecimal,

    @Column(name = "change_amount", precision = 15, scale = 4)
    var changeAmount: BigDecimal? = null,

    @Column(name = "change_rate", precision = 7, scale = 4)
    var changeRate: BigDecimal? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant? = null,
)
