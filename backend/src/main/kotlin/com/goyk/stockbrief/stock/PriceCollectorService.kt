package com.goyk.stockbrief.stock

import com.goyk.stockbrief.common.exception.NotFoundException
import com.goyk.stockbrief.market.YahooFinanceClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Service
@Transactional(readOnly = true)
class PriceCollectorService(
    private val stockRepository: StockRepository,
    private val dailyPriceRepository: DailyPriceRepository,
    private val finnhubClient: FinnhubClient,
    private val yahooFinanceClient: YahooFinanceClient,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val seoulZone: ZoneId = ZoneId.of("Asia/Seoul")

    @Transactional
    fun collectOne(stockId: Long): DailyPrice {
        val stock = stockRepository.findById(stockId)
            .orElseThrow { NotFoundException("Stock not found: id=$stockId") }
        return collect(stock)
    }

    @Transactional
    fun collectAll(): CollectResult {
        val stocks = stockRepository.findAll()
        val collected = mutableListOf<DailyPrice>()
        val failed = mutableListOf<FailedItem>()

        for (stock in stocks) {
            try {
                collected.add(collect(stock))
            } catch (e: Exception) {
                log.error("Failed to collect price for ${stock.ticker}: ${e.message}", e)
                failed.add(FailedItem(ticker = stock.ticker, reason = e.message ?: "unknown"))
            }
        }

        return CollectResult(collected = collected.size, failed = failed)
    }

    private fun collect(stock: Stock): DailyPrice {
        val today = LocalDate.now(seoulZone)

        if (dailyPriceRepository.existsByStockAndDate(stock, today)) {
            log.info("Price already exists for ${stock.ticker} on $today, skip")
            return dailyPriceRepository.findByStockAndDate(stock, today)
                ?: error("Inconsistent state: existsByStockAndDate true but findByStockAndDate null")
        }

        val quote = finnhubClient.fetchQuote(stock.ticker)
        val dailyPrice = DailyPrice(
            stock = stock,
            date = today,
            openingPrice = quote.open.toBigDecimal2(),
            closingPrice = quote.currentPrice.toBigDecimal2(),
            highPrice = quote.high.toBigDecimal2(),
            lowPrice = quote.low.toBigDecimal2(),
            changeRate = quote.changePercent?.toBigDecimal4(),
            volume = null,
        )
        return dailyPriceRepository.save(dailyPrice)
    }

    @Transactional
    fun backfillFromYahoo(stockId: Long, range: String = "1mo"): BackfillResult {
        val stock = stockRepository.findById(stockId)
            .orElseThrow { NotFoundException("Stock not found: id=$stockId") }
        return backfill(stock, range)
    }

    @Transactional
    fun backfillAll(range: String = "1mo"): BackfillSummary {
        val stocks = stockRepository.findAll()
        var totalSaved = 0
        val failed = mutableListOf<FailedItem>()

        for (stock in stocks) {
            try {
                totalSaved += backfill(stock, range).saved
            } catch (e: Exception) {
                log.error("Failed to backfill ${stock.ticker}: ${e.message}", e)
                failed.add(FailedItem(stock.ticker, e.message ?: "unknown"))
            }
        }

        return BackfillSummary(totalSaved = totalSaved, failed = failed)
    }

    private fun backfill(stock: Stock, range: String): BackfillResult {
        val response = yahooFinanceClient.fetchChart(stock.ticker, range, "1d")
        val data = response.chart.result?.firstOrNull()
            ?: throw IllegalStateException("No chart data for ${stock.ticker}")

        val timestamps = data.timestamp ?: emptyList()
        val quote = data.indicators.quote.firstOrNull()
            ?: throw IllegalStateException("No quote data for ${stock.ticker}")

        var saved = 0
        var skipped = 0

        for (i in timestamps.indices) {
            val open = quote.open?.getOrNull(i)
            val high = quote.high?.getOrNull(i)
            val low = quote.low?.getOrNull(i)
            val close = quote.close?.getOrNull(i)
            val volume = quote.volume?.getOrNull(i)

            if (open == null || high == null || low == null || close == null) {
                continue
            }

            val date = Instant.ofEpochSecond(timestamps[i])
                .atZone(ZoneId.of("America/New_York"))
                .toLocalDate()

            if (dailyPriceRepository.existsByStockAndDate(stock, date)) {
                skipped++
                continue
            }

            dailyPriceRepository.save(
                DailyPrice(
                    stock = stock,
                    date = date,
                    openingPrice = open.toBigDecimal2(),
                    closingPrice = close.toBigDecimal2(),
                    highPrice = high.toBigDecimal2(),
                    lowPrice = low.toBigDecimal2(),
                    changeRate = null,
                    volume = volume,
                ),
            )
            saved++
        }

        return BackfillResult(ticker = stock.ticker, saved = saved, skipped = skipped)
    }

    private fun Double.toBigDecimal2(): BigDecimal = BigDecimal(this).setScale(2, RoundingMode.HALF_UP)

    private fun Double.toBigDecimal4(): BigDecimal = BigDecimal(this).setScale(4, RoundingMode.HALF_UP)
}

data class BackfillResult(
    val ticker: String,
    val saved: Int,
    val skipped: Int,
)

data class BackfillSummary(
    val totalSaved: Int,
    val failed: List<FailedItem>,
)

data class CollectResult(
    val collected: Int,
    val failed: List<FailedItem>,
)

data class FailedItem(
    val ticker: String,
    val reason: String,
)
