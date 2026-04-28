package com.goyk.stockbrief.stock

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface DailyPriceRepository : JpaRepository<DailyPrice, Long> {
    fun findByStockOrderByDateDesc(stock: Stock): List<DailyPrice>

    fun findByStockAndDate(stock: Stock, date: LocalDate): DailyPrice?

    fun existsByStockAndDate(stock: Stock, date: LocalDate): Boolean
}
