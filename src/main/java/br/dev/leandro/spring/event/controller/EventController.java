package br.dev.leandro.spring.event.controller;

import br.dev.leandro.spring.event.controller.dto.EventDto;
import br.dev.leandro.spring.event.entity.Event;
import br.dev.leandro.spring.event.mapper.EventMapper;
import br.dev.leandro.spring.event.service.EventService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PutMapping("/{id}")
    public ResponseEntity<EventDto> update(@PathVariable Long id, @RequestBody EventDto eventDto) {
        Event event = eventService.update(id, eventDto);
        return ResponseEntity.ok(eventMapper.toDto(event));
    }
    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getById(@PathVariable Long id) {
        EventDto eventDto = eventService.getById(id);
        return ResponseEntity.ok(eventDto);
    }

    @GetMapping
    public ResponseEntity<Iterable<EventDto>> listEvents(Pageable pageable) {
        Iterable<EventDto> eventDtos = eventService.getAll(pageable);
        return ResponseEntity.ok(eventDtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
