package com.goyk.stockbrief.stock

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class StockCreateRequest(
    @field:NotBlank(message = "ticker는 필수입니다")
    @field:Size(max = 20, message = "ticker는 최대 20자")
    val ticker: String,

    @field:NotBlank(message = "name은 필수입니다")
    @field:Size(max = 100, message = "name은 최대 100자")
    val name: String,

    @field:NotBlank(message = "market은 필수입니다")
    @field:Size(max = 20, message = "market은 최대 20자")
    val market: String,

    val memo: String? = null,
) {
    fun toEntity(): Stock = Stock(
        ticker = ticker,
        name = name,
        market = market,
        memo = memo,
    )
}
