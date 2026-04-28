package com.goyk.stockbrief.market

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

/**
 * Yahoo Finance 비공식 endpoint 클라이언트.
 *
 * 사용 endpoint:
 *   GET /v8/finance/chart/{ticker}?range={range}&interval={interval}
 *
 * 동작 ticker 예:
 *   주식  : AAPL, MSFT, 005930.KS, 000660.KS
 *   인덱스: ^GSPC (S&P500), ^IXIC (NASDAQ), ^KS11 (KOSPI), ^KQ11 (KOSDAQ), ^VIX
 *   환율  : KRW=X (USD/KRW), JPY=X
 *   원자재: CL=F (WTI 원유), GC=F (금)
 *   달러  : DX-Y.NYB (DXY)
 *
 * User-Agent 명시: Yahoo가 default Java User-Agent를 봇으로 차단하는 사례 회피.
 */
@Component
class YahooFinanceClient {
    private val client: RestClient = RestClient.builder()
        .baseUrl("https://query1.finance.yahoo.com")
        .defaultHeader("User-Agent", "Mozilla/5.0")
        .build()

    fun fetchChart(ticker: String, range: String = "5d", interval: String = "1d"): YahooChartResponse {
        return client.get()
            .uri(
                "/v8/finance/chart/{ticker}?range={range}&interval={interval}",
                ticker,
                range,
                interval,
            )
            .retrieve()
            .body(YahooChartResponse::class.java)
            ?: throw IllegalStateException("Empty response from Yahoo for $ticker")
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class YahooChartResponse(
    val chart: ChartResult,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChartResult(
    val result: List<ChartData>?,
    val error: Any?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChartData(
    val meta: ChartMeta,
    val timestamp: List<Long>?,
    val indicators: ChartIndicators,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChartMeta(
    val symbol: String,
    val currency: String?,
    @JsonProperty("regularMarketPrice") val regularMarketPrice: Double?,
    @JsonProperty("chartPreviousClose") val chartPreviousClose: Double?,
    @JsonProperty("longName") val longName: String?,
    @JsonProperty("shortName") val shortName: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChartIndicators(
    val quote: List<ChartQuote>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChartQuote(
    val open: List<Double?>?,
    val high: List<Double?>?,
    val low: List<Double?>?,
    val close: List<Double?>?,
    val volume: List<Long?>?,
)
