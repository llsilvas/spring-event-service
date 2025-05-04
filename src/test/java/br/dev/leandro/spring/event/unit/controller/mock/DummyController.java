package br.dev.leandro.spring.event.unit.controller.mock;

import br.dev.leandro.spring.event.exception.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador fictício usado para testes de tratamento de exceções.
 * Este controlador expõe endpoints que lançam diferentes tipos de exceções
 * para testar o comportamento do GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/dummy")
public class DummyController {

    /**
     * Lança uma exceção de acesso negado.
     * Usado para testar o tratamento de AccessDeniedException.
     */
    @GetMapping("/access-denied")
    public void throwAccessDenied() {
        throw new AccessDeniedException("Access denied");
    }

    /**
     * Lança uma exceção de autenticação falha.
     * Usado para testar o tratamento de AuthenticationException.
     */
    @GetMapping("/authentication-failed")
    public void throwAuthenticationFailed() {
        throw new AuthenticationException("Unauthorized") {
        };
    }

    /**
     * Lança uma exceção de recurso não encontrado.
     * Usado para testar o tratamento de ResourceNotFoundException.
     */
    @GetMapping("/resource-not-found")
    public void throwNotFound() {
        throw new ResourceNotFoundException("Resource not found");
    }

    /**
     * Lança uma exceção de requisição inválida.
     * Usado para testar o tratamento de IllegalArgumentException.
     */
    @GetMapping("/bad-request")
    public void throwBadRequest() {
        throw new IllegalArgumentException("Invalid argument provided");
    }

    /**
     * Lança uma exceção de erro interno do servidor.
     * Usado para testar o tratamento de RuntimeException.
     */
    @GetMapping("/server-error")
    public void throwServerError() {
        throw new RuntimeException("Internal server error");
    }
}