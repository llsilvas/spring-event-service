package br.dev.leandro.spring.event.controller.dto;

import br.dev.leandro.spring.event.entity.EventStatus;

import java.time.LocalDateTime;

public record EventDto(
        Long id,
        String name,
        String description,
        LocalDateTime startDatetime,
        LocalDateTime endDatetime,
        String location,
        EventStatus status
) {}
