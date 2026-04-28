package com.goyk.stockbrief.market

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/market-indices")
class MarketIndexController(
    private val marketIndexCollectorService: MarketIndexCollectorService,
) {
    @GetMapping("/latest")
    fun findLatest(): List<MarketIndexResponse> {
        return marketIndexCollectorService.findLatestAll().map(MarketIndexResponse::from)
    }

    @PostMapping("/collect")
    fun collectAll(): MarketCollectResult {
        return marketIndexCollectorService.collectAll()
    }
}
