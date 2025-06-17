package br.dev.leandro.spring.event.service;

import br.dev.leandro.spring.event.dto.EventDto;
import br.dev.leandro.spring.event.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface EventService {

    Event create(String userId, EventDto dto);
    Event update (UUID id, EventDto dto);
    EventDto getById(UUID id);

    Page<EventDto> getAll(Pageable pageable);

    void delete(UUID id);

}
