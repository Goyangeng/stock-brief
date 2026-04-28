package com.goyk.stockbrief.stock

import com.goyk.stockbrief.common.exception.ConflictException
import com.goyk.stockbrief.common.exception.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class StockService(
    private val stockRepository: StockRepository,
) {
    fun findAll(): List<Stock> {
        return stockRepository.findAll()
    }

    fun findById(id: Long): Stock {
        return stockRepository.findById(id)
            .orElseThrow { NotFoundException("Stock not found: id=$id") }
    }

    @Transactional
    fun create(request: StockCreateRequest): Stock {
        if (stockRepository.existsByTicker(request.ticker)) {
            throw ConflictException("Stock already exists: ticker=${request.ticker}")
        }
        return stockRepository.save(request.toEntity())
    }

    @Transactional
    fun delete(id: Long) {
        if (!stockRepository.existsById(id)) {
            throw NotFoundException("Stock not found: id=$id")
        }
        stockRepository.deleteById(id)
    }
}
