package com.goyk.stockbrief

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class StockBriefApplication

fun main(args: Array<String>) {
    runApplication<StockBriefApplication>(*args)
}
