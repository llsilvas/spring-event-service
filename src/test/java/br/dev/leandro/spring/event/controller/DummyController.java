package br.dev.leandro.spring.event.controller;

import br.dev.leandro.spring.event.exception.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dummy")
public class DummyController {

    @GetMapping("/access-denied")
    public void throwAccessDenied() {
        throw new AccessDeniedException("Access denied");
    }

    @GetMapping("/authentication-failed")
    public void throwAuthenticationFailed() {
        throw new AuthenticationException("Unauthorized") {
        };
    }

    @GetMapping("/resource-not-found")
    public void throwNotFound() {
        throw new ResourceNotFoundException("Resource not found");
    }

    @GetMapping("/bad-request")
    public void throwBadRequest() {
        throw new IllegalArgumentException("Invalid argument provided");
    }

    @GetMapping("/server-error")
    public void throwServerError() {
        throw new RuntimeException("Internal server error");
    }

}
