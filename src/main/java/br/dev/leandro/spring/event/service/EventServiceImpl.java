package br.dev.leandro.spring.event.service;

import br.dev.leandro.spring.event.controller.dto.EventDto;
import br.dev.leandro.spring.event.entity.Event;
import br.dev.leandro.spring.event.exception.ResourceNotFoundException;
import br.dev.leandro.spring.event.mapper.EventMapper;
import br.dev.leandro.spring.event.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    public Event create(EventDto dto) {
        Event event = eventMapper.toEntity(dto);
        return eventRepository.save(event);
    }

    @Override
    public Event update(Long id, EventDto dto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado!"));
        eventMapper.updateEntityFromDto(dto, event);
        return eventRepository.save(event);
    }

    @Override
    public EventDto getById(Long id) {
        return eventRepository.findById(id)
                .map(eventMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado!"));
    }

    @Override
    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        eventRepository.deleteById(id);
    }
}
