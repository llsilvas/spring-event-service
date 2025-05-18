package br.dev.leandro.spring.event.config;


import br.dev.leandro.spring.event.utils.GitPropertiesLoader;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Instant;
import java.util.Properties;

@Slf4j
@Configuration
public class OpenApiConfig {

    @Value("${app.name:}")
    private String appName;
    @Value("${app.description:}")
    private String appDescription;
    @Value("${app.contact.name:}")
    private String contactName;
    @Value("${app.title}")
    private String appTitle;

    private final GitPropertiesLoader gitPropertiesLoader;

    public OpenApiConfig(GitPropertiesLoader gitPropertiesLoader) {
        this.gitPropertiesLoader = gitPropertiesLoader;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(info());
    }

    private Info info() {
        Properties properties = new Properties();
        properties.put("git.check.time", Instant.now().toString());

        try{
            Properties loadProperties = gitPropertiesLoader.loadProperties();

            if (loadProperties != null){
                properties.putAll(loadProperties);
            }else{
                throw new IOException("git.properties nao encontrado");
            }
        } catch (IOException e) {
            log.info("Erro ao carregar arquivo: {} ", e.getMessage());
        }

        String versionNumber = properties.getProperty("git.build.version");
        String commitId = properties.getProperty("git.commit.id.abbrev");
        String commitTime = properties.getProperty("git.commit.time");
        String branch = properties.getProperty("git.branch");
        String tags = properties.getProperty("git.tags");

        log.info("::Commit Branch: {}", branch);
        log.info("::Git ID Commit: {}", commitId);
        log.info("::Git Time Commit: {}", commitTime);

        String realVersion = (tags != null && !tags.isEmpty())? tags:versionNumber;

        return new Info()
                .contact(new Contact().name(contactName))
                .title(appTitle)
                .version(realVersion)
                .description(appDescription);


    }
}
