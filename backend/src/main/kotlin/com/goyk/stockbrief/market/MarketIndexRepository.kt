package com.goyk.stockbrief.market

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface MarketIndexRepository : JpaRepository<MarketIndex, Long> {
    fun findBySymbolOrderByDateDesc(symbol: String): List<MarketIndex>

    fun findFirstBySymbolOrderByDateDesc(symbol: String): MarketIndex?

    fun findBySymbolAndDate(symbol: String, date: LocalDate): MarketIndex?

    fun existsBySymbolAndDate(symbol: String, date: LocalDate): Boolean
}
