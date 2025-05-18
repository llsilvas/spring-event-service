package br.dev.leandro.spring.event.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

@Slf4j
@Component
public class GitPropertiesLoader {

    public Properties loadProperties() throws IOException {
        ClassPathResource resource = new ClassPathResource("/git.properties");
        Properties props = new Properties();

        if( resource.exists()){
            props.load(resource.getInputStream());
        }else {
            log.info("Arquivo não encontrado");
        }

        return props;
    }
}
