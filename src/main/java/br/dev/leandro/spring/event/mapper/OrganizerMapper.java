package br.dev.leandro.spring.event.mapper;

import br.dev.leandro.spring.event.controller.dto.OrganizerCreateDto;
import br.dev.leandro.spring.event.controller.dto.OrganizerDto;
import br.dev.leandro.spring.event.controller.dto.OrganizerUpdateDto;
import br.dev.leandro.spring.event.entity.Organizer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrganizerMapper {

    Organizer toEntity(OrganizerCreateDto organizerCreateDto);

    OrganizerDto toDto(Organizer organizer);

    OrganizerCreateDto toCreateDto(Organizer organizer);

    void updateEntityFromUpdateDto(OrganizerUpdateDto dto, @MappingTarget Organizer entity);
}
