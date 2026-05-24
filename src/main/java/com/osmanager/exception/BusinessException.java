package com.osmanager.exception;

/**
 * Thrown when a business rule is violated.
 * Examples:
 *   - Creating a service order for an inactive client
 *   - Cancelling an already-completed order
 *   - Registering a duplicate CPF
 *
 * Maps to HTTP 422 Unprocessable Entity (or 400 Bad Request) in the handler.
 *
 * Note: unlike ResourceNotFoundException (404 — resource doesn't exist),
 * BusinessException means the resource EXISTS but the OPERATION is invalid.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
