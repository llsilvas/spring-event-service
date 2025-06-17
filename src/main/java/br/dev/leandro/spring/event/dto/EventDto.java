package br.dev.leandro.spring.event.dto;

import br.dev.leandro.spring.event.entity.enums.EventStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Future;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for Event entity.
 *
 * @param id Identificador único do evento
 * @param name Nome do evento
 * @param description Descrição do evento
 * @param startDatetime Data e hora de início
 * @param endDatetime Data e hora de término
 * @param location Local do evento
 * @param status Status do evento
 */
public record EventDto(
        UUID id,

        @NotBlank(message = "Nome do evento é obrigatório")
        @Size(message = "Nome deve ter tamanho adequado")
        String name,

        @Size(message = "Descrição deve ter tamanho adequado")
        String description,

        @NotNull(message = "Data de início é obrigatória")
        @Future(message = "Data de início deve ser no futuro")
        LocalDateTime startDatetime,

        @NotNull(message = "Data de término é obrigatória")
        @Future(message = "Data de término deve ser no futuro")
        LocalDateTime endDatetime,

        @NotBlank(message = "Local é obrigatório")
        @Size(message = "Local deve ter tamanho adequado")
        String location,

        EventStatus status
) { }
