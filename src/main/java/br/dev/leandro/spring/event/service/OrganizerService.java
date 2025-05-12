package br.dev.leandro.spring.event.service;

import br.dev.leandro.spring.event.controller.dto.OrganizerCreateDto;
import br.dev.leandro.spring.event.controller.dto.OrganizerUpdateDto;
import br.dev.leandro.spring.event.entity.Organizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrganizerService {
    
    Organizer create(String userId, OrganizerCreateDto organizerCreateDto);
    Organizer update(Long id, OrganizerUpdateDto organizerUpdateDto);
    OrganizerCreateDto getById(Long id);
    
    Page<OrganizerCreateDto> getAll(Pageable pageable);
    
    void delete(Long id);
}
