package br.dev.leandro.spring.event.service;

import br.dev.leandro.spring.event.controller.dto.EventDto;
import br.dev.leandro.spring.event.entity.Event;
import br.dev.leandro.spring.event.entity.EventStatus;
import br.dev.leandro.spring.event.exception.ResourceNotFoundException;
import br.dev.leandro.spring.event.mapper.EventMapper;
import br.dev.leandro.spring.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    public static final String EVENT_NOT_FOUND_MESSAGE = "Evento não encontrado!";
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    public Event create(EventDto dto) {
        Event event = eventMapper.toEntity(dto);
        return eventRepository.save(event);
    }

    @Override
    public Event update(Long id, EventDto dto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_NOT_FOUND_MESSAGE));
        eventMapper.updateEntityFromDto(dto, event);
        return eventRepository.save(event);
    }

    @Override
    public EventDto getById(Long id) {
        return eventRepository.findById(id)
                .map(eventMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_NOT_FOUND_MESSAGE));
    }

    @Override
    public Page<EventDto> getAll(Pageable pageable) {
        return eventRepository.findAll(pageable)
                .map(eventMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        eventRepository.findById(id)
                .map(event -> {
                    event.setStatus(EventStatus.DELETED);
                    return eventRepository.save(event);
                })
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_NOT_FOUND_MESSAGE));
    }
}
