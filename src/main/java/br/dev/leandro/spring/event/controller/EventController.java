package br.dev.leandro.spring.event.controller;

import br.dev.leandro.spring.event.controller.dto.EventDto;
import br.dev.leandro.spring.event.entity.Event;
import br.dev.leandro.spring.event.mapper.EventMapper;
import br.dev.leandro.spring.event.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;

    public EventController(EventService eventService, EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }

    @PostMapping
    public ResponseEntity<EventDto> create(@RequestBody EventDto eventDto) {
        Event event = eventService.create(eventDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventMapper.toDto(event));
    }
}
