package br.dev.leandro.spring.event.unit.exception;

import br.dev.leandro.spring.event.exception.ApiErrorResponse;
import br.dev.leandro.spring.event.exception.ResourceNotFoundException;
import br.dev.leandro.spring.event.exception.handler.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Testes unitários para o GlobalExceptionHandler.
 * Verifica se as exceções são tratadas corretamente e retornam as respostas HTTP apropriadas.
 */
@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/test-uri");
    }

    @Nested
    @DisplayName("Testes de exceções de segurança")
    class SecurityExceptionTests {
        @Test
        @DisplayName("Deve retornar 403 quando o acesso é negado")
        void shouldReturn403WhenAccessDenied() {
            // Given
            AccessDeniedException exception = new AccessDeniedException("Acesso negado");

            // When
            ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleAccessDeniedException(exception, request);

            // Then
            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            ApiErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals(403, errorResponse.getStatus());
            assertEquals("Forbidden", errorResponse.getError());
            assertEquals("Acesso negado", errorResponse.getMessage());
            assertEquals("/test-uri", errorResponse.getPath());
        }

        @Test
        @DisplayName("Deve retornar 401 quando a autenticação falha")
        void shouldReturn401WhenAuthenticationFails() {
            // Given
            AuthenticationException exception = new AuthenticationException("Não autorizado") {};

            // When
            ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleAuthenticationException(exception, request);

            // Then
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            ApiErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals(401, errorResponse.getStatus());
            assertEquals("Unauthorized", errorResponse.getError());
            assertEquals("Não autorizado", errorResponse.getMessage());
            assertEquals("/test-uri", errorResponse.getPath());
        }
    }

    @Nested
    @DisplayName("Testes de exceções de recursos")
    class ResourceExceptionTests {
        @Test
        @DisplayName("Deve retornar 404 quando o recurso não é encontrado")
        void shouldReturn404WhenResourceNotFound() {
            // Given
            ResourceNotFoundException exception = new ResourceNotFoundException("Recurso não encontrado");

            // When
            ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleResourceNotFound(exception, request);

            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            ApiErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals(404, errorResponse.getStatus());
            assertEquals("Not Found", errorResponse.getError());
            assertEquals("Recurso não encontrado", errorResponse.getMessage());
            assertEquals("/test-uri", errorResponse.getPath());
        }
    }

    @Nested
    @DisplayName("Testes de exceções de validação")
    class ValidationExceptionTests {
        @Test
        @DisplayName("Deve retornar 400 quando os argumentos são inválidos")
        void shouldReturn400WhenBadRequest() {
            // Given
            IllegalArgumentException exception = new IllegalArgumentException("Argumento inválido");

            // When
            ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleIllegalArgumentException(exception, request);

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            ApiErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals(400, errorResponse.getStatus());
            assertEquals("Bad Request", errorResponse.getError());
            assertEquals("Argumento inválido", errorResponse.getMessage());
            assertEquals("/test-uri", errorResponse.getPath());
        }

        @Test
        @DisplayName("Deve retornar 400 com detalhes de validação quando há erros de validação")
        void shouldReturn400WithValidationDetails() {
            // Given
            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);
            FieldError fieldError = new FieldError("object", "field", "mensagem de erro");

            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

            // When
            ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleValidationExceptions(exception, request);

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            ApiErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals(400, errorResponse.getStatus());
            assertEquals("Bad Request", errorResponse.getError());
            assertEquals("/test-uri", errorResponse.getPath());
        }
    }

    @Nested
    @DisplayName("Testes de exceções genéricas")
    class GenericExceptionTests {
        @Test
        @DisplayName("Deve retornar 500 quando ocorre um erro interno")
        void shouldReturn500WhenServerError() {
            // Given
            RuntimeException exception = new RuntimeException("Erro interno do servidor");

            // When
            ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleGenericException(exception, request);

            // Then
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            ApiErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals(500, errorResponse.getStatus());
            assertEquals("Internal Server Error", errorResponse.getError());
            assertEquals("Erro interno do servidor", errorResponse.getMessage());
            assertEquals("/test-uri", errorResponse.getPath());
        }
    }
}
