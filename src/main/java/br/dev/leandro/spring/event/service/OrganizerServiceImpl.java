package br.dev.leandro.spring.event.service;

import br.dev.leandro.spring.event.controller.dto.OrganizerDto;
import br.dev.leandro.spring.event.entity.Organizer;
import br.dev.leandro.spring.event.entity.OrganizerStatus;
import br.dev.leandro.spring.event.exception.ResourceNotFoundException;
import br.dev.leandro.spring.event.mapper.OrganizerMapper;
import br.dev.leandro.spring.event.repository.OrganizerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrganizerServiceImpl implements OrganizerService {

    public static final String ORGANIZER_NOT_FOUND_MESSAGE = "Organizador não encontrado!";
    private final OrganizerRepository organizerRepository;
    private final OrganizerMapper organizerMapper;

    public OrganizerServiceImpl(OrganizerRepository organizerRepository, OrganizerMapper organizerMapper) {
        this.organizerRepository = organizerRepository;
        this.organizerMapper = organizerMapper;
    }

    @Override
    public Organizer create(OrganizerDto organizerDto) {
        Organizer organizer = organizerMapper.toEntity(organizerDto);
        return organizerRepository.save(organizer);
    }

    @Override
    public Organizer update(Long id, OrganizerDto organizerDto) {
        Organizer organizer = organizerRepository.findByIdAndStatus(id, OrganizerStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException(ORGANIZER_NOT_FOUND_MESSAGE));
        organizerMapper.updateEntityFromDto(organizerDto, organizer);
        return organizerRepository.save(organizer);
    }

    @Override
    public OrganizerDto getById(Long id) {
        return organizerRepository.findByIdAndStatus(id, OrganizerStatus.ACTIVE)
                .map(organizerMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(ORGANIZER_NOT_FOUND_MESSAGE));
    }

    @Override
    public Page<OrganizerDto> getAll(Pageable pageable) {
        return organizerRepository.findAllByStatus(OrganizerStatus.ACTIVE, pageable)
                .map(organizerMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        organizerRepository.findByIdAndStatus(id, OrganizerStatus.ACTIVE)
                .map(organizer -> {
                    organizer.setStatus(OrganizerStatus.DELETED);
                    return organizerRepository.save(organizer);
                })
                .orElseThrow(() -> new ResourceNotFoundException(ORGANIZER_NOT_FOUND_MESSAGE));
    }
}
