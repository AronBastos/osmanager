package com.osmanager.exception;

/**
 * Thrown when a requested resource (Client, OrdemServico, etc.) does not exist.
 * Maps to HTTP 404 Not Found in the GlobalExceptionHandler.
 *
 * ==============================================================
 * EXCEPTION DESIGN — INTERVIEW NOTE (Topic 8)
 * ==============================================================
 * Extends RuntimeException (unchecked) — Spring's @Transactional
 * ONLY rolls back for unchecked exceptions by default.
 * (You can override this with rollbackFor = Exception.class, but
 * the convention is to use unchecked exceptions for runtime failures.)
 *
 * Q: "Why use custom exceptions instead of generic ones?"
 * A: Custom exceptions carry semantic meaning — the name tells you
 *    exactly what went wrong. They also allow @ExceptionHandler to
 *    catch them specifically and return precise HTTP status codes.
 * ==============================================================
 */
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceType;
    private final Object resourceId;

    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceType = "Unknown";
        this.resourceId = null;
    }

    public ResourceNotFoundException(String resourceType, Object resourceId) {
        super(resourceType + " not found with id: " + resourceId);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public String getResourceType() { return resourceType; }
    public Object getResourceId() { return resourceId; }
}
