package br.dev.leandro.spring.event.controller.dto;

import br.dev.leandro.spring.event.entity.enums.OrganizerStatus;

public record OrganizerUpdateDto(
        String organizationName,
        String contactEmail,
        String contactPhone,
        String documentNumber,
        OrganizerStatus status
) {}

