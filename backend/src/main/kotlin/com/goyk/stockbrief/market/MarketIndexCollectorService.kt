package com.goyk.stockbrief.market

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.ZoneId

@Service
@Transactional(readOnly = true)
class MarketIndexCollectorService(
    private val marketIndexRepository: MarketIndexRepository,
    private val yahooFinanceClient: YahooFinanceClient,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val seoulZone: ZoneId = ZoneId.of("Asia/Seoul")

    private val targetIndices = listOf(
        IndexTarget("^IXIC", "NASDAQ Composite"),
        IndexTarget("^VIX", "CBOE Volatility Index"),
        IndexTarget("DX-Y.NYB", "US Dollar Index"),
        IndexTarget("^KS11", "KOSPI"),
        IndexTarget("KRW=X", "USD/KRW"),
    )

    fun findLatestAll(): List<MarketIndex> {
        return targetIndices.mapNotNull { target ->
            marketIndexRepository.findFirstBySymbolOrderByDateDesc(target.symbol)
        }
    }

    @Transactional
    fun collectAll(): MarketCollectResult {
        val collected = mutableListOf<MarketIndex>()
        val failed = mutableListOf<MarketFailedItem>()

        for (target in targetIndices) {
            try {
                collected.add(collect(target))
            } catch (e: Exception) {
                log.error("Failed to collect ${target.symbol}: ${e.message}", e)
                failed.add(MarketFailedItem(symbol = target.symbol, reason = e.message ?: "unknown"))
            }
        }

        return MarketCollectResult(collected = collected.size, failed = failed)
    }

    private fun collect(target: IndexTarget): MarketIndex {
        val today = LocalDate.now(seoulZone)

        if (marketIndexRepository.existsBySymbolAndDate(target.symbol, today)) {
            log.info("MarketIndex already exists for ${target.symbol} on $today, skip")
            return marketIndexRepository.findBySymbolAndDate(target.symbol, today)
                ?: error("Inconsistent state for ${target.symbol}")
        }

        val response = yahooFinanceClient.fetchChart(target.symbol, range = "5d", interval = "1d")
        val data = response.chart.result?.firstOrNull()
            ?: throw IllegalStateException("No chart data for ${target.symbol}")

        val price = data.meta.regularMarketPrice
            ?: throw IllegalStateException("No regularMarketPrice for ${target.symbol}")
        val previousClose = data.meta.chartPreviousClose

        val changeAmount = previousClose?.let { (price - it).toBigDecimal4() }
        val changeRate = previousClose?.takeIf { it != 0.0 }
            ?.let { ((price - it) / it * 100).toBigDecimal4() }

        val marketIndex = MarketIndex(
            symbol = target.symbol,
            name = target.name,
            date = today,
            closingPrice = price.toBigDecimal4(),
            changeAmount = changeAmount,
            changeRate = changeRate,
        )
        return marketIndexRepository.save(marketIndex)
    }

    private fun Double.toBigDecimal4(): BigDecimal = BigDecimal(this).setScale(4, RoundingMode.HALF_UP)
}

private data class IndexTarget(
    val symbol: String,
    val name: String,
)

data class MarketCollectResult(
    val collected: Int,
    val failed: List<MarketFailedItem>,
)

data class MarketFailedItem(
    val symbol: String,
    val reason: String,
)
