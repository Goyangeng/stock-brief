package com.goyk.stockbrief.stock

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/prices")
class PriceController(
    private val priceCollectorService: PriceCollectorService,
) {
    @PostMapping("/collect/{stockId}")
    @ResponseStatus(HttpStatus.CREATED)
    fun collectOne(@PathVariable stockId: Long): DailyPriceResponse {
        val price = priceCollectorService.collectOne(stockId)
        return DailyPriceResponse.from(price)
    }

    @PostMapping("/collect")
    fun collectAll(): CollectResult {
        return priceCollectorService.collectAll()
    }
}
