package br.dev.leandro.spring.event.repository;

import br.dev.leandro.spring.event.controller.dto.OrganizerDto;
import br.dev.leandro.spring.event.entity.Organizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface OrganizerRepository extends PagingAndSortingRepository<Organizer, Long> {

    Organizer save(Organizer organizer);

    Optional<Organizer> findById(Long id);

    Page<OrganizerDto> getAll(Pageable pageable);
}
