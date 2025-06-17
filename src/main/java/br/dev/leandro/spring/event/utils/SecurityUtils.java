package br.dev.leandro.spring.event.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;


public class SecurityUtils {

    private SecurityUtils() {
    }

    public static Jwt getJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || !(auth.getPrincipal() instanceof Jwt)){
            throw new IllegalStateException("Jwt não encontrado no contexto de segurança");
        }
        return (Jwt) auth.getPrincipal();
    }

    public static String getUser() {
        Object claim = getJwt().getClaim("preferred_username");
        if (claim == null) {
            throw new IllegalStateException("Claim 'preferred_username' não encontrado no JWT");
        }
        return claim.toString();
    }

}
