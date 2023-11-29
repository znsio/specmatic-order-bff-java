package com.component.orders.handlers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.client.ResourceAccessException
import java.time.LocalDateTime

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        var badRequest = HttpStatus.BAD_REQUEST
        if (ex is ResourceAccessException)
            badRequest = HttpStatus.SERVICE_UNAVAILABLE
        val errorResponse = ErrorResponse(
            LocalDateTime.now(),
            badRequest.value(),
            "An error occurred while processing the request",
            ex.message ?: "Unknown error"
        )
        return ResponseEntity.status(badRequest).body(errorResponse)
    }
}

data class ErrorResponse(
    val timestamp: LocalDateTime,
    val status: Int,
    val error: String,
    val message: String
)
