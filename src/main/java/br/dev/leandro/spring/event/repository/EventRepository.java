package br.dev.leandro.spring.event.repository;

import br.dev.leandro.spring.event.controller.dto.EventDto;
import br.dev.leandro.spring.event.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface EventRepository extends PagingAndSortingRepository<Event, Long> {
    Event save(Event event);

    Optional<Event> findById(Long id);

    public Page<EventDto> getAll(Pageable pageable);
}
