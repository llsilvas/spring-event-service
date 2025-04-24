package br.dev.leandro.spring.event.service;

import br.dev.leandro.spring.event.controller.dto.EventDto;
import br.dev.leandro.spring.event.entity.Event;

import java.util.List;

public interface EventService {

    Event create(EventDto dto);
    Event update (Long id, EventDto dto);
    EventDto getById(Long id);
    List<Event> getAll();
    void delete(Long id);

}
