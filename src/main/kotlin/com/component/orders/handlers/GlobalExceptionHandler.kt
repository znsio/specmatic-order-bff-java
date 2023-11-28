package com.component.orders.handlers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.ResourceAccessException
import java.time.LocalDateTime

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        val (httpStatus, message) = when(ex) {
            is HttpClientErrorException -> Pair(ex.statusCode, "An error occurred while processing the request")
            else -> Pair(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error")
        }

        val errorResponse = ErrorResponse(
            LocalDateTime.now(),
            httpStatus.value(),
            message,
            ex.message ?: "Unknown error"
        )
        return ResponseEntity.status(httpStatus).body(errorResponse)
    }
}

data class ErrorResponse(
    val timestamp: LocalDateTime,
    val status: Int,
    val error: String,
    val message: String
)
