package br.dev.leandro.spring.event.config;

import br.dev.leandro.spring.event.converter.CustomJwtAuthenticationConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
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
@EnableMethodSecurity
@RequiredArgsConstructor
public class LocalSecurityConfig {

    private final CustomJwtAuthenticationConverter customJwtAuthenticationConverter;

    @Bean
    public SecurityFilterChain localSecurityFilterChain(HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
        log.info("Configurando filtros de segurança para perfil local - todos os endpoints liberados");

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/event/swagger-ui/**").permitAll()
                        .requestMatchers("/event/swagger-ui.html").permitAll()
                        .requestMatchers("/event/api-docs/**").permitAll()
                        .requestMatchers("/organizers/**").hasAnyRole("ADMIN", "ORGANIZADOR")
                        .anyRequest().permitAll() // Permite acesso a todos os endpoints sem autenticação
                ).oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        log.info("Filtros de segurança para perfil local configurados com sucesso");
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