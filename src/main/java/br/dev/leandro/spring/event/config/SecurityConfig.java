package br.dev.leandro.spring.event.config;

import br.dev.leandro.spring.event.converter.CustomJwtAuthenticationConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de segurança da aplicação.
 * Esta classe define as regras de autenticação e autorização para os endpoints da API.
 * CSRF está desabilitado por ser uma API stateless com autenticação via JWT.
 */
@Slf4j
@Configuration
@Profile("!test")
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomJwtAuthenticationConverter customJwtAuthenticationConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configurando filtros de segurança");
        http
                .csrf(AbstractHttpConfigurer::disable) // Desabilitar CSRF para APIs
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/event/swagger-ui/**").permitAll()
                        .requestMatchers("/event/swagger-ui.html").permitAll()
                        .requestMatchers("/event/api-docs/**").permitAll()
                        // Endpoints com restrições específicas
                        .requestMatchers("/event/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/event/api/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated() // Qualquer outra requisição deve estar autenticada
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.error("Erro de autenticação: {}", authException.getMessage());
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write("{\"error\":\"Não autorizado\"}");
                        })
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()) // Configuração JWT
                        )
                );

        log.info("Filtros de segurança configurados com sucesso");
        return http.build();
    }

    /**
     * Configura o conversor de autenticação JWT para extrair as autoridades (roles) do token.
     * Utiliza um conversor personalizado para mapear as claims do JWT para as autoridades do Spring Security.
     *
     * @return O conversor de autenticação JWT configurado
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        log.debug("Configurando conversor de autenticação JWT");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(customJwtAuthenticationConverter);
        return converter;
    }
}
