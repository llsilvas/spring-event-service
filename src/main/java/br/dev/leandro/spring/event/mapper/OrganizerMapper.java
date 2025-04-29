package br.dev.leandro.spring.event.mapper;

import br.dev.leandro.spring.event.controller.dto.OrganizerDto;
import br.dev.leandro.spring.event.entity.Organizer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrganizerMapper {

    OrganizerMapper organizerMapper = Mappers.getMapper(OrganizerMapper.class);

    Organizer toEntity(OrganizerDto organizerDto);

    OrganizerDto toDto(Organizer organizer);

    void updateEntityFromDto(OrganizerDto organizerDto, @MappingTarget Organizer organizer);
}
