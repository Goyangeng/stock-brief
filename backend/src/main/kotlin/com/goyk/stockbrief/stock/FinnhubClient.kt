package com.goyk.stockbrief.stock

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

/**
 * Finnhub /quote response.
 *
 * 필드 의미:
 *  c  current price
 *  d  change (price diff)
 *  dp change percent
 *  h  high of the day
 *  l  low of the day
 *  o  open of the day
 *  pc previous close
 *  t  unix timestamp (sec)
 */
data class FinnhubQuote(
    @JsonProperty("c") val currentPrice: Double,
    @JsonProperty("d") val change: Double?,
    @JsonProperty("dp") val changePercent: Double?,
    @JsonProperty("h") val high: Double,
    @JsonProperty("l") val low: Double,
    @JsonProperty("o") val open: Double,
    @JsonProperty("pc") val previousClose: Double?,
    @JsonProperty("t") val timestamp: Long?,
)

@Component
class FinnhubClient(
    @Value("\${finnhub.api-key}") private val apiKey: String,
    @Value("\${finnhub.base-url}") private val baseUrl: String,
) {
    private val client: RestClient = RestClient.builder().baseUrl(baseUrl).build()

    fun fetchQuote(ticker: String): FinnhubQuote {
        require(apiKey.isNotBlank()) { "FINNHUB_API_KEY is not set" }
        return client.get()
            .uri("/quote?symbol={symbol}&token={token}", ticker, apiKey)
            .retrieve()
            .body(FinnhubQuote::class.java)
            ?: throw IllegalStateException("Empty response from Finnhub for $ticker")
    }
}
