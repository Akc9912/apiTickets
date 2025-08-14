package com.poo.miapi.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;

import jakarta.persistence.EntityNotFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Maneja errores de validación de datos recibidos en el cuerpo de la petición
    // (por ejemplo, cuando un DTO tiene anotaciones como @NotNull y no se cumplen).
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
    body.put("error", "Validación fallida");
    body.put("status", 400);
    body.put("details", ex.getBindingResult().getFieldErrors().stream()
        .map(err -> Map.of("field", err.getField(), "message", err.getDefaultMessage()))
        .toList());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Captura errores cuando el tipo de argumento en la URL o parámetros no coincide con lo
    // esperado (por ejemplo, se espera un número y se recibe texto).
    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Tipo de argumento incorrecto");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Se activa cuando no se encuentra una entidad en la base de datos
    // (por ejemplo, buscar un usuario por ID y no existe).
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(EntityNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Recurso no encontrado");
        body.put("status", 404);
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // Maneja argumentos inválidos pasados a métodos (por ejemplo, valores fuera de rango).
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Argumento inválido");
        body.put("status", 400);
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    // Captura cualquier otra excepción no manejada por los métodos anteriores.
    // Registra el error en el log, incluye información sobre la ruta y el tipo de excepción.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) throws Exception {
        String path = null;
        try {
            path = (String) RequestContextHolder.currentRequestAttributes()
                .getAttribute("org.springframework.web.servlet.HandlerMapping.pathWithinHandlerMapping", 0);
        } catch (Exception ignored) {}

        Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
        logger.error("[GlobalExceptionHandler] Excepción capturada en ruta: {}", path);
        logger.error("[GlobalExceptionHandler] Tipo: {} - Mensaje: {}", ex.getClass().getName(), ex.getMessage(), ex);

        // Si la ruta es de Swagger/OpenAPI, deja que Spring maneje la excepción
        if (path != null && (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui"))) {
            logger.warn("[GlobalExceptionHandler] Lanzando excepción original para ruta Swagger/OpenAPI: {}", path);
            throw ex;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("error", "Error interno del servidor");
        body.put("type", ex.getClass().getSimpleName());
        body.put("status", 500);
        body.put("message", "Ha ocurrido un error inesperado. Intente más tarde.");
        body.put("exception", ex.getMessage());
        body.put("path", path);
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}