package br.dev.leandro.spring.event.exception.handler;

import br.dev.leandro.spring.event.exception.ApiErrorResponse;
import br.dev.leandro.spring.event.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public final class GlobalExceptionHandler {

    /**
     * Trata exceções de acesso negado.
     *
     * @param e Exceção lançada
     * @param request Requisição HTTP
     * @return Resposta de erro
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(
            final AccessDeniedException e, final HttpServletRequest request) {
        return buildErrorResponse(e, HttpStatus.FORBIDDEN, e.getMessage(), request);
    }

    /**
     * Trata exceções de autenticação.
     *
     * @param e Exceção lançada
     * @param request Requisição HTTP
     * @return Resposta de erro
     */
    @ExceptionHandler(AuthenticationException.class)
            public ResponseEntity<ApiErrorResponse> handleAuthenticationException(
            final AuthenticationException e, final HttpServletRequest request) {
        return buildErrorResponse(e, HttpStatus.UNAUTHORIZED, e.getMessage(), request);
    }

    /**
     * Trata exceções genéricas.
     *
     * @param e Exceção lançada
     * @param request Requisição HTTP
     * @return Resposta de erro
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            final Exception e, final HttpServletRequest request) {
        return buildErrorResponse(
                e, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), request);
    }

    /**
     * Trata exceções de recurso não encontrado.
     *
     * @param ex Exceção lançada
     * @param request Requisição HTTP
     * @return Resposta de erro
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            final ResourceNotFoundException ex, final HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    /**
     * Trata exceções de argumento inválido.
     *
     * @param ex Exceção lançada
     * @param request Requisição HTTP
     * @return Resposta de erro
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
            final IllegalArgumentException ex, final HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    /**
     * Trata exceções de validação de argumentos.
     *
     * @param ex Exceção lançada
     * @param request Requisição HTTP
     * @return Resposta de erro
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(
            final MethodArgumentNotValidException ex, 
            final HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String message = "Erro de validação: " + errors;
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, message, request);
    }

    /**
     * Constrói uma resposta de erro padronizada.
     *
     * @param e Exceção lançada
     * @param status Status HTTP
     * @param message Mensagem de erro
     * @param request Requisição HTTP
     * @return Resposta de erro
     */
    private ResponseEntity<ApiErrorResponse> buildErrorResponse(
            final Exception e, 
            final HttpStatus status, 
            final String message, 
            final HttpServletRequest request) {
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();
        log.error("Exception handled: {}", errorResponse, e);
        return ResponseEntity.status(status).body(errorResponse);
    }
}
