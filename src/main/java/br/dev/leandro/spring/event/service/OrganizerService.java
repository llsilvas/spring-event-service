package br.dev.leandro.spring.event.service;

import br.dev.leandro.spring.event.controller.dto.OrganizerDto;
import br.dev.leandro.spring.event.entity.Organizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrganizerService {
    
    Organizer create(OrganizerDto organizerDto);
    Organizer update(Long id, OrganizerDto organizerDto);
    OrganizerDto getById(Long id);
    
    Page<OrganizerDto> getAll(Pageable pageable);
    
    void delete(Long id);
}
