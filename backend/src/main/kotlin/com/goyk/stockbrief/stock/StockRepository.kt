package com.goyk.stockbrief.stock

import org.springframework.data.jpa.repository.JpaRepository

interface StockRepository : JpaRepository<Stock, Long> {
    fun findByTicker(ticker: String): Stock?

    fun existsByTicker(ticker: String): Boolean
}
