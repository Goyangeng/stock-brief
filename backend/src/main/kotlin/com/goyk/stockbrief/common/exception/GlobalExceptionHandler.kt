package com.goyk.stockbrief.common.exception

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(
        e: NotFoundException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse(
                code = "NOT_FOUND",
                message = e.message ?: "Resource not found",
                path = request.requestURI,
            ),
        )
    }

    @ExceptionHandler(ConflictException::class)
    fun handleConflict(
        e: ConflictException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            ErrorResponse(
                code = "CONFLICT",
                message = e.message ?: "Resource conflict",
                path = request.requestURI,
            ),
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(
        e: MethodArgumentNotValidException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        val fieldErrors = e.bindingResult.fieldErrors.map {
            ErrorResponse.FieldError(
                field = it.field,
                rejectedValue = it.rejectedValue,
                message = it.defaultMessage ?: "Invalid value",
            )
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                code = "VALIDATION_FAILED",
                message = "Validation failed",
                path = request.requestURI,
                errors = fieldErrors,
            ),
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(
        e: Exception,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        log.error("Unhandled exception: ${e.message}", e)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse(
                code = "INTERNAL_ERROR",
                message = "Internal server error",
                path = request.requestURI,
            ),
        )
    }
}
