package br.dev.leandro.spring.event.service;

import br.dev.leandro.spring.event.dto.OrganizerCreateDto;
import br.dev.leandro.spring.event.dto.OrganizerDto;
import br.dev.leandro.spring.event.dto.OrganizerUpdateDto;
import br.dev.leandro.spring.event.entity.Organizer;
import br.dev.leandro.spring.event.entity.enums.OrganizerStatus;
import br.dev.leandro.spring.event.exception.BusinessException;
import br.dev.leandro.spring.event.exception.ResourceNotFoundException;
import br.dev.leandro.spring.event.mapper.OrganizerMapper;
import br.dev.leandro.spring.event.repository.OrganizerRepository;
import br.dev.leandro.spring.event.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
        if(organizerCreateDto == null){
            throw new IllegalArgumentException("OrganizerCreateDto não pode ser nulo.");
        }
        if(userId == null || userId.isBlank()){
            throw new IllegalArgumentException("UserId não pode ser nulo.");
        }

        String user = SecurityUtils.getUser();
        if(organizerRepository.existsByUserId(userId)){
            throw new BusinessException("Organizador já existe.");
        }
        Organizer organizer = organizerMapper.toEntity(organizerCreateDto);
        organizer.setUserId(userId);
        organizer.setStatus(OrganizerStatus.ACTIVE);
        organizer.setCreatedBy(user);
        return organizerRepository.save(organizer);
    }

    @Override
    public Organizer update(UUID id, OrganizerUpdateDto organizerUpdateDto, String userId){
        String user = SecurityUtils.getUser();
        Organizer organizer = organizerRepository.findByIdAndStatus(id, OrganizerStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException(ORGANIZER_NOT_FOUND_MESSAGE));

        if(!organizer.getUserId().equals(userId) && !isAdmin()){
            throw new AccessDeniedException("Você não tem permissão para atualizar este organizador");
        }
        organizerMapper.updateEntityFromUpdateDto(organizerUpdateDto, organizer);
        organizer.setUpdatedBy(user);
        return organizerRepository.save(organizer);
    }

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    @Override
    public OrganizerDto getById(UUID id) {
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
    public void delete(UUID id) {
        organizerRepository.findByIdAndStatus(id, OrganizerStatus.ACTIVE)
                .map(organizer -> {
                    organizer.setStatus(OrganizerStatus.DELETED);
                    return organizerRepository.save(organizer);
                })
                .orElseThrow(() -> new ResourceNotFoundException(ORGANIZER_NOT_FOUND_MESSAGE));
    }
}
