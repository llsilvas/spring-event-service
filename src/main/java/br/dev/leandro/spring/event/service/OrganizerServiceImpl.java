package br.dev.leandro.spring.event.service;

import br.dev.leandro.spring.event.controller.dto.OrganizerCreateDto;
import br.dev.leandro.spring.event.controller.dto.OrganizerUpdateDto;
import br.dev.leandro.spring.event.entity.Organizer;
import br.dev.leandro.spring.event.entity.enums.OrganizerStatus;
import br.dev.leandro.spring.event.exception.BusinessException;
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
    public Organizer create(String userId, OrganizerCreateDto organizerCreateDto) {
        if(organizerRepository.existsByUserId(userId)){
            throw new BusinessException("Organizador já existe.");
        }
        Organizer organizer = organizerMapper.toEntity(organizerCreateDto);
        return organizerRepository.save(organizer);
    }

    @Override
    public Organizer update(Long id, OrganizerUpdateDto organizerUpdateDto) {
        Organizer organizer = organizerRepository.findByIdAndStatus(id, OrganizerStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException(ORGANIZER_NOT_FOUND_MESSAGE));
        organizerMapper.updateEntityFromUpdateDto(organizerUpdateDto, organizer);
        return organizerRepository.save(organizer);
    }

    @Override
    public OrganizerCreateDto getById(Long id) {
        return organizerRepository.findByIdAndStatus(id, OrganizerStatus.ACTIVE)
                .map(organizerMapper::toCreateDto)
                .orElseThrow(() -> new ResourceNotFoundException(ORGANIZER_NOT_FOUND_MESSAGE));
    }

    @Override
    public Page<OrganizerCreateDto> getAll(Pageable pageable) {
        return organizerRepository.findAllByStatus(OrganizerStatus.ACTIVE, pageable)
                .map(organizerMapper::toCreateDto);
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
