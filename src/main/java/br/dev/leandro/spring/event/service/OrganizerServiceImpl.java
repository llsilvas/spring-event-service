package br.dev.leandro.spring.event.service;

import br.dev.leandro.spring.event.controller.dto.OrganizerDto;
import br.dev.leandro.spring.event.entity.Organizer;
import br.dev.leandro.spring.event.entity.OrganizerStatus;
import br.dev.leandro.spring.event.exception.ResourceNotFoundException;
import br.dev.leandro.spring.event.mapper.OrganizerMapper;
import br.dev.leandro.spring.event.repository.OrganizerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizerServiceImpl implements OrganizerService {

    public static final String ORGANIZER_NOT_FOUND_MESSAGE = "Organizador não encontrado!";
    private final OrganizerRepository organizerRepository;
    private final OrganizerMapper organizerMapper;

    @Override
    public Organizer create(OrganizerDto organizerDto) {
        Organizer organizer = organizerMapper.toEntity(organizerDto);
        return organizerRepository.save(organizer);
    }

    @Override
    public Organizer update(Long id, OrganizerDto organizerDto) {
        Organizer organizer = organizerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORGANIZER_NOT_FOUND_MESSAGE));
        organizerMapper.updateEntityFromDto(organizerDto, organizer);
        return organizerRepository.save(organizer);
    }

    @Override
    public OrganizerDto getById(Long id) {
        return organizerRepository.findById(id)
                .map(organizerMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(ORGANIZER_NOT_FOUND_MESSAGE));
    }

    @Override
    public Page<OrganizerDto> getAll(Pageable pageable) {
        return organizerRepository.findAll(pageable)
                .map(organizerMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        organizerRepository.findById(id)
                .map(organizer -> {
                    organizer.setStatus(OrganizerStatus.DELETED);
                    return organizerRepository.save(organizer);
                })
                .orElseThrow(() -> new ResourceNotFoundException(ORGANIZER_NOT_FOUND_MESSAGE));
    }
}
