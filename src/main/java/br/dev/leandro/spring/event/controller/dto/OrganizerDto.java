package br.dev.leandro.spring.event.controller.dto;

import br.dev.leandro.spring.event.entity.Event;
import br.dev.leandro.spring.event.entity.OrganizerRole;
import br.dev.leandro.spring.event.entity.User;

public record OrganizerDto(
        Long id,
        User user,
        Event event,
        OrganizerRole role
) {
}
