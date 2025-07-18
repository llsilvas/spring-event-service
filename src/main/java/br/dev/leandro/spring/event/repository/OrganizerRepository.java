package br.dev.leandro.spring.event.repository;

import br.dev.leandro.spring.event.entity.Organizer;
import br.dev.leandro.spring.event.entity.enums.OrganizerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrganizerRepository extends PagingAndSortingRepository<Organizer, UUID> {

    Optional<Organizer> findByIdAndStatus(UUID id, OrganizerStatus organizerStatus);

    Page<Organizer> findAllByStatus(OrganizerStatus organizerStatus, Pageable pageable);

    Organizer save(Organizer organizer);

    boolean existsByUserId(String userId);
}
