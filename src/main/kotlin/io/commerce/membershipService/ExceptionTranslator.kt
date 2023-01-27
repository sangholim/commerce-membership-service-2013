package io.commerce.membershipService

import io.commerce.membershipService.core.ErrorCodeException
import io.commerce.membershipService.core.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebInputException

@RestControllerAdvice
class ExceptionTranslator {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleWebExchangeBindException(exception: WebExchangeBindException): ResponseEntity<ErrorResponse> {
        log.debug("WebExchangeBindException", exception)
        return ResponseEntity
            .badRequest()
            .contentType(MediaType.APPLICATION_JSON)
            .body(ErrorResponse.from(exception))
    }

    @ExceptionHandler(ErrorCodeException::class)
    fun handleErrorCodeException(exception: ErrorCodeException): ResponseEntity<ErrorResponse> {
        log.debug("ErrorCodeException", exception)
        return ResponseEntity
            .status(exception.status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ErrorResponse.from(exception))
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(exception: ResponseStatusException): ResponseEntity<ErrorResponse> {
        log.debug("ResponseStatusException", exception)
        return ResponseEntity
            .status(exception.status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ErrorResponse.from(exception))
    }

    @ExceptionHandler(ServerWebInputException::class)
    fun handleServerWebInputException(exception: ServerWebInputException): ResponseEntity<ErrorResponse> {
        log.debug("ServerWebInputException", exception)
        return ResponseEntity
            .badRequest()
            .contentType(MediaType.APPLICATION_JSON)
            .body(ErrorResponse.ofBadRequest())
    }

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<ErrorResponse> {
        log.error("Unhandled exception", exception)
        return ResponseEntity
            .internalServerError()
            .contentType(MediaType.APPLICATION_JSON)
            .body(ErrorResponse.ofInternal())
    }
}
