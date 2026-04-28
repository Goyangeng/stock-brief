package com.goyk.stockbrief.stock

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class PriceCollectionScheduler(
    private val priceCollectorService: PriceCollectorService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 매일 한국 시간 07:00 정각에 등록된 모든 종목의 가격을 수집한다.
     *
     * cron 형식: "초 분 시 일 월 요일" (Spring 6자리, Linux cron과 다름)
     * zone="Asia/Seoul" 명시 — 서버 OS 타임존이 UTC여도 한국 시간 기준 동작.
     */
    @Scheduled(cron = "0 0 7 * * *", zone = "Asia/Seoul")
    fun collectDailyPrices() {
        log.info("Daily price collection started")
        val result = priceCollectorService.collectAll()
        log.info(
            "Daily price collection done: collected={}, failed={}",
            result.collected,
            result.failed.size,
        )
        if (result.failed.isNotEmpty()) {
            result.failed.forEach { log.warn("Failed: ticker={}, reason={}", it.ticker, it.reason) }
        }
    }
}
