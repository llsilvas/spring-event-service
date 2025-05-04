package br.dev.leandro.spring.event.controller.dto;

import br.dev.leandro.spring.event.entity.Event;
import br.dev.leandro.spring.event.entity.OrganizerRole;
import br.dev.leandro.spring.event.entity.User;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Data Transfer Object for Organizer entity.
 *
 * @param id Identificador único do organizador
 * @param user Usuário associado ao organizador
 * @param events Lista de eventos associados ao organizador
 * @param role Papel do organizador nos eventos
 */
public record OrganizerDto(
        Long id,

        @NotNull(message = "Usuário é obrigatório")
        User user,

        @NotNull(message = "Eventos são obrigatórios")
        List<Event> events,

        @NotNull(message = "Papel do organizador é obrigatório")
        OrganizerRole role
) {
}
