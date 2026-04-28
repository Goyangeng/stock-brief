package com.goyk.stockbrief.market

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MarketIndexCollectionScheduler(
    private val marketIndexCollectorService: MarketIndexCollectorService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 매일 한국 시간 07:30에 시장 지표 수집.
     * 가격 수집(07:00)보다 30분 늦게 — 미국 시장 마감 후 데이터 안정화 대기.
     */
    @Scheduled(cron = "0 30 7 * * *", zone = "Asia/Seoul")
    fun collectDailyIndices() {
        log.info("Daily market indices collection started")
        val result = marketIndexCollectorService.collectAll()
        log.info(
            "Daily market indices collection done: collected={}, failed={}",
            result.collected,
            result.failed.size,
        )
        if (result.failed.isNotEmpty()) {
            result.failed.forEach { log.warn("Failed: symbol={}, reason={}", it.symbol, it.reason) }
        }
    }
}
