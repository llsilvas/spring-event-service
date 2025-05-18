package br.dev.leandro.spring.event.dto;

import br.dev.leandro.spring.event.entity.enums.OrganizerStatus;

import java.time.LocalDateTime;

public record OrganizerDto(
        Long id,
        String userId,
        String organizationName,
        String contactEmail,
        String contactPhone,
        String documentNumber,
        OrganizerStatus status,
        String createdBy,
        String updatedBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

