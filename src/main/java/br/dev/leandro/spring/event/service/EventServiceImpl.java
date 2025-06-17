package br.dev.leandro.spring.event.service;

import br.dev.leandro.spring.event.dto.EventDto;
import br.dev.leandro.spring.event.entity.Event;
import br.dev.leandro.spring.event.entity.enums.EventStatus;
import br.dev.leandro.spring.event.exception.ResourceNotFoundException;
import br.dev.leandro.spring.event.mapper.EventMapper;
import br.dev.leandro.spring.event.repository.EventRepository;
import br.dev.leandro.spring.event.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EventServiceImpl implements EventService {
    public static final String EVENT_NOT_FOUND_MESSAGE = "Evento não encontrado!";
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    public Event create(String userId, EventDto dto) {
        String user = SecurityUtils.getUser();

        Event event = eventMapper.toEntity(dto);
        event.setStatus(EventStatus.DRAFT);
        event.setOrganizerId(UUID.fromString(userId));
        event.setCreatedBy(user);

        return eventRepository.save(event);
    }

    @Override
    public Event update(UUID id, EventDto dto) {
        String user = SecurityUtils.getUser();
        Event event = eventRepository.findByIdAndStatus(id, EventStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_NOT_FOUND_MESSAGE));
        event.setUpdatedBy(user);
        eventMapper.updateEntityFromDto(dto, event);
        return eventRepository.save(event);
    }

    @Override
    public EventDto getById(UUID id) {
        return eventRepository.findByIdAndStatus(id, EventStatus.ACTIVE)
                .map(eventMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_NOT_FOUND_MESSAGE));
    }

    @Override
    public Page<EventDto> getAll(Pageable pageable) {
        return eventRepository.findAll(pageable)
                .map(eventMapper::toDto);
    }

    @Override
    public void delete(UUID id) {
        eventRepository.findByIdAndStatus(id, EventStatus.ACTIVE)
                .map(event -> {
                    event.setStatus(EventStatus.DELETED);
                    return eventRepository.save(event);
                })
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_NOT_FOUND_MESSAGE));
    }
}
