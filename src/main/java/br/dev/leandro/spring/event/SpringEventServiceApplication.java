package br.dev.leandro.spring.event;

import br.dev.leandro.spring.event.security.keycloak.KeycloakProperties;
import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Log
@EnableConfigurationProperties(KeycloakProperties.class)
@SpringBootApplication
public class SpringEventServiceApplication {

    public static void main(String[] args) {
        log.info(":: Iniciando Spring-Event-Service ::");
        long startTime = System.currentTimeMillis(); // Captura o tempo de início

        SpringApplication.run(SpringEventServiceApplication.class, args);
        long endTime = System.currentTimeMillis(); // Captura o tempo de fim
        long totalTime = endTime - startTime; // Calcula o tempo total em milissegundos
        log.info(":: Spring-Event-Service iniciado com sucesso :: - " + totalTime + " s" );
    }

}
