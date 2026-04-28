package com.goyk.stockbrief.common.exception

import java.time.Instant

data class ErrorResponse(
    val code: String,
    val message: String,
    val path: String,
    val timestamp: Instant = Instant.now(),
    val errors: List<FieldError>? = null,
) {
    data class FieldError(
        val field: String,
        val rejectedValue: Any?,
        val message: String,
    )
}
