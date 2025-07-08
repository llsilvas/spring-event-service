package br.dev.leandro.spring.event.controller;

import br.dev.leandro.spring.event.config.SecurityTestConfig;
import br.dev.leandro.spring.event.dto.EventDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SecurityTestConfig.class)
class EventControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void create() {
    }

    @Test
    void update() {
    }

    @Test
    void getById() {
    }

    @Test
    void listEvents() {
        ResponseEntity<EventDto[]> response = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("/events", EventDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void delete() {
    }
}