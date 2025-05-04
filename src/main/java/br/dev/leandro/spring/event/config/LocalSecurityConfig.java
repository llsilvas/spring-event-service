package br.dev.leandro.spring.event.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de segurança para o perfil local.
 * Esta classe libera todos os endpoints para testes no ambiente local,
 * desabilitando a autenticação e autorização.
 */
@Slf4j
@Configuration
@Profile("local")
@EnableWebSecurity
public class LocalSecurityConfig {

    @Bean
    public SecurityFilterChain localSecurityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configurando filtros de segurança para perfil local - todos os endpoints liberados");
        
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Permite acesso a todos os endpoints sem autenticação
            );
        
        log.info("Filtros de segurança para perfil local configurados com sucesso");
        return http.build();
    }
}