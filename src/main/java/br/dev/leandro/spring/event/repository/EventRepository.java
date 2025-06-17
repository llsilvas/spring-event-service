package br.dev.leandro.spring.event.repository;

import br.dev.leandro.spring.event.entity.Event;
import br.dev.leandro.spring.event.entity.enums.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends PagingAndSortingRepository<Event, UUID> {

    Optional<Event> findByIdAndStatus(UUID id, EventStatus status);

    Page<Event> findAllByStatus(EventStatus status, Pageable pageable);

    Event save(Event event);
}
