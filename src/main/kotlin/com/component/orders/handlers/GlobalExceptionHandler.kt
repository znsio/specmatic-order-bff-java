package com.component.orders.handlers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.servlet.resource.NoResourceFoundException
import java.time.LocalDateTime

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        val badRequest = when (ex) {
            is ResourceAccessException -> HttpStatus.SERVICE_UNAVAILABLE
            is NoResourceFoundException -> HttpStatus.NOT_FOUND
            else -> HttpStatus.BAD_REQUEST
        }
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
