package com.goyk.stockbrief.stock

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/stocks")
class StockController(
    private val stockService: StockService,
) {
    @GetMapping
    fun findAll(): List<StockResponse> {
        return stockService.findAll().map(StockResponse::from)
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): StockResponse {
        return StockResponse.from(stockService.findById(id))
    }

    @GetMapping("/{stockId}/prices")
    fun findPrices(@PathVariable stockId: Long): List<DailyPriceResponse> {
        return stockService.findPricesByStockId(stockId).map(DailyPriceResponse::from)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: StockCreateRequest): StockResponse {
        val stock = stockService.create(request)
        return StockResponse.from(stock)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        stockService.delete(id)
    }
}
