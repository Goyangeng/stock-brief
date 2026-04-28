package com.goyk.stockbrief.stock

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class StockService(
    private val stockRepository: StockRepository,
) {
    fun findAll(): List<Stock> = stockRepository.findAll()

    fun findById(id: Long): Stock = stockRepository.findById(id)
        .orElseThrow { NoSuchElementException("Stock not found: id=$id") }

    @Transactional
    fun create(request: StockCreateRequest): Stock {
        require(!stockRepository.existsByTicker(request.ticker)) {
            "Ticker already exists: ${request.ticker}"
        }
        return stockRepository.save(request.toEntity())
    }

    @Transactional
    fun delete(id: Long) {
        if (!stockRepository.existsById(id)) {
            throw NoSuchElementException("Stock not found: id=$id")
        }
        stockRepository.deleteById(id)
    }
}
