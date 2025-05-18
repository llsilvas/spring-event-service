package br.dev.leandro.spring.event.service;

import br.dev.leandro.spring.event.dto.OrganizerCreateDto;
import br.dev.leandro.spring.event.dto.OrganizerUpdateDto;
import br.dev.leandro.spring.event.entity.Organizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.security.access.AccessDeniedException;

public interface OrganizerService {

    Organizer create(String userId, OrganizerCreateDto organizerCreateDto);
    Organizer update(Long id, OrganizerUpdateDto organizerUpdateDto, String userId) throws AccessDeniedException;
    OrganizerCreateDto getById(Long id);

    Page<OrganizerCreateDto> getAll(Pageable pageable);

    void delete(Long id);
}
