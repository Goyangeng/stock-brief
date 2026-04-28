package com.goyk.stockbrief.stock

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
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "daily_prices")
@EntityListeners(AuditingEntityListener::class)
class DailyPrice(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock_id", nullable = false)
    var stock: Stock,

    @Column(nullable = false)
    var date: LocalDate,

    @Column(name = "opening_price", nullable = false, precision = 15, scale = 2)
    var openingPrice: BigDecimal,

    @Column(name = "closing_price", nullable = false, precision = 15, scale = 2)
    var closingPrice: BigDecimal,

    @Column(name = "high_price", nullable = false, precision = 15, scale = 2)
    var highPrice: BigDecimal,

    @Column(name = "low_price", nullable = false, precision = 15, scale = 2)
    var lowPrice: BigDecimal,

    @Column(name = "change_rate", precision = 7, scale = 4)
    var changeRate: BigDecimal? = null,

    @Column
    var volume: Long? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant? = null,
)
