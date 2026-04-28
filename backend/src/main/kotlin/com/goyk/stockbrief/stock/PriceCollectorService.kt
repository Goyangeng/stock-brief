package com.goyk.stockbrief.stock

import com.goyk.stockbrief.common.exception.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.ZoneId

@Service
@Transactional(readOnly = true)
class PriceCollectorService(
    private val stockRepository: StockRepository,
    private val dailyPriceRepository: DailyPriceRepository,
    private val finnhubClient: FinnhubClient,
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

    private fun Double.toBigDecimal2(): BigDecimal = BigDecimal(this).setScale(2, RoundingMode.HALF_UP)

    private fun Double.toBigDecimal4(): BigDecimal = BigDecimal(this).setScale(4, RoundingMode.HALF_UP)
}

data class CollectResult(
    val collected: Int,
    val failed: List<FailedItem>,
)

data class FailedItem(
    val ticker: String,
    val reason: String,
)
