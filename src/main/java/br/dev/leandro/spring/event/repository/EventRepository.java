package br.dev.leandro.spring.event.repository;

import br.dev.leandro.spring.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
