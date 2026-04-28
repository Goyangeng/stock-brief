package com.goyk.stockbrief.stock

import java.time.Instant

data class StockResponse(
    val id: Long,
    val ticker: String,
    val name: String,
    val market: String,
    val memo: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        fun from(stock: Stock): StockResponse = StockResponse(
            id = requireNotNull(stock.id) { "Stock id must not be null after persistence" },
            ticker = stock.ticker,
            name = stock.name,
            market = stock.market,
            memo = stock.memo,
            createdAt = requireNotNull(stock.createdAt),
            updatedAt = requireNotNull(stock.updatedAt),
        )
    }
}
