package com.goyk.stockbrief.stock

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

data class DailyPriceResponse(
    val id: Long,
    val stockId: Long,
    val ticker: String,
    val date: LocalDate,
    val openingPrice: BigDecimal,
    val closingPrice: BigDecimal,
    val highPrice: BigDecimal,
    val lowPrice: BigDecimal,
    val changeRate: BigDecimal?,
    val volume: Long?,
    val createdAt: Instant,
) {
    companion object {
        fun from(dailyPrice: DailyPrice): DailyPriceResponse = DailyPriceResponse(
            id = requireNotNull(dailyPrice.id),
            stockId = requireNotNull(dailyPrice.stock.id),
            ticker = dailyPrice.stock.ticker,
            date = dailyPrice.date,
            openingPrice = dailyPrice.openingPrice,
            closingPrice = dailyPrice.closingPrice,
            highPrice = dailyPrice.highPrice,
            lowPrice = dailyPrice.lowPrice,
            changeRate = dailyPrice.changeRate,
            volume = dailyPrice.volume,
            createdAt = requireNotNull(dailyPrice.createdAt),
        )
    }
}
