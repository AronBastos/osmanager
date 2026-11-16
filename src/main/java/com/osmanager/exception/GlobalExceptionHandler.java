package com.osmanager.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ==============================================================
 * GLOBAL EXCEPTION HANDLER — INTERVIEW NOTE (Topic 8)
 * ==============================================================
 * NEVER put try/catch blocks in controllers. Centralise ALL error
 * handling here. This keeps controllers THIN and error responses CONSISTENT.
 *
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 *   → Intercepts exceptions from ALL @RestController classes
 *   → Each @ExceptionHandler method maps one exception type to an HTTP response
 *
 * Q: "How does exception handling work in Spring Boot?"
 * A: @RestControllerAdvice intercepts exceptions thrown by any controller.
 *    Each @ExceptionHandler method maps an exception class to a specific
 *    HTTP status and structured response body. This centralises error
 *    handling, avoids duplication, and ensures consistent API error format.
 *
 * Q: "What is the difference between @ControllerAdvice and @RestControllerAdvice?"
 * A: @RestControllerAdvice is @ControllerAdvice + @ResponseBody.
 *    Use @RestControllerAdvice for REST APIs (returns JSON bodies).
 *    Use @ControllerAdvice for MVC apps that return views.
 * ==============================================================
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ==============================================================
    // ERROR RESPONSE BODY — Structured API error format
    // ==============================================================
    // All error responses return the same JSON structure:
    // {
    //   "timestamp": "2025-01-15T10:30:00",
    //   "status": 404,
    //   "error": "Not Found",
    //   "message": "Client not found with id: 42",
    //   "path": null
    // }
    //
    // This is consistent and parseable by API clients.
    // Spring Boot 3 also supports ProblemDetail (RFC 7807) as an alternative.
    // ==============================================================

    /**
     * Handles ResourceNotFoundException → 404 Not Found.
     *
     * @ExceptionHandler declares which exception class this method handles.
     * Spring calls this method whenever that exception propagates up to the
     * DispatcherServlet without being caught elsewhere.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles BusinessException → 422 Unprocessable Entity.
     * HTTP 422 means: syntax was fine, but the business rule was violated.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(BusinessException ex) {
        return buildError(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    /**
     * Handles @Valid validation failures → 400 Bad Request.
     *
     * ==============================================================
     * VALIDATION FLOW — INTERVIEW NOTE (Topic 5)
     * ==============================================================
     * 1. Client sends JSON body to @PostMapping endpoint
     * 2. Controller method has @Valid @RequestBody ClientRequestDTO dto
     * 3. Spring runs Bean Validation constraints (@NotBlank, @Email, etc.)
     * 4. If ANY field fails → MethodArgumentNotValidException is thrown
     * 5. THIS method catches it and returns a structured 400 response
     *    with field-level error details.
     *
     * Response example:
     * {
     *   "status": 400,
     *   "error": "Validation Failed",
     *   "message": "Input validation failed",
     *   "fieldErrors": {
     *     "name": "Name is required",
     *     "email": "Email must be a valid address"
     *   }
     * }
     * ==============================================================
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        // ==============================================================
        // STREAMS ON VALIDATION ERRORS — INTERVIEW NOTE (Topic 2)
        // ==============================================================
        // ex.getBindingResult().getFieldErrors() returns List<FieldError>
        // We stream it to build a Map<fieldName, errorMessage> in one pipeline:
        //
        // .stream()                           → open stream
        // .collect(Collectors.toMap(          → terminal: build a Map
        //     FieldError::getField,           → key = field name (method ref)
        //     FieldError::getDefaultMessage,  → value = message
        //     (existing, replacement) -> existing  → merge fn (keep first error per field)
        // ))
        // ==============================================================
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing  // keep first error per field
                ));

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Failed");
        body.put("message", "Input validation failed");
        body.put("fieldErrors", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Catch-all handler → 500 Internal Server Error.
     * Catches any unhandled exception — prevents stack traces leaking to clients.
     * In production: log the full exception HERE, return only a generic message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        // Erros do próprio Spring MVC (parâmetro ausente, método não suportado, rota inexistente...)
        // implementam ErrorResponse e já carregam o status HTTP correto (4xx).
        // Sem este desvio, o catch-all os transformaria em 500.
        if (ex instanceof ErrorResponse errorResponse) {
            HttpStatusCode status = errorResponse.getStatusCode();
            return buildError(HttpStatus.valueOf(status.value()), errorResponse.getBody().getDetail());
        }

        // SECURITY: Never expose ex.getMessage() to the client in production.
        // O detalhe vai para o log; o cliente recebe só uma mensagem genérica.
        log.error("Unhandled exception", ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.");
    }

    // ---- Helper to build a consistent error response body ----

    private ResponseEntity<Map<String, Object>> buildError(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    status.value());
        body.put("error",     status.getReasonPhrase());
        body.put("message",   message);
        return ResponseEntity.status(status).body(body);
    }
}
