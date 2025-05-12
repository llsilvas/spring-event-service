package br.dev.leandro.spring.event.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OrganizerCreateDto(
        @NotBlank String organizationName,
        @Email String contactEmail,
        @NotBlank String contactPhone,
        @NotBlank String documentNumber
) {
}
