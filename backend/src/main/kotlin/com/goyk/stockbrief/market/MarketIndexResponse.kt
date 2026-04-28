package com.goyk.stockbrief.market

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

data class MarketIndexResponse(
    val id: Long,
    val symbol: String,
    val name: String,
    val date: LocalDate,
    val closingPrice: BigDecimal,
    val changeAmount: BigDecimal?,
    val changeRate: BigDecimal?,
    val createdAt: Instant,
) {
    companion object {
        fun from(index: MarketIndex): MarketIndexResponse = MarketIndexResponse(
            id = requireNotNull(index.id),
            symbol = index.symbol,
            name = index.name,
            date = index.date,
            closingPrice = index.closingPrice,
            changeAmount = index.changeAmount,
            changeRate = index.changeRate,
            createdAt = requireNotNull(index.createdAt),
        )
    }
}
