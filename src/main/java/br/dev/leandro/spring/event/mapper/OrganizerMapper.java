package br.dev.leandro.spring.event.mapper;

import br.dev.leandro.spring.event.dto.OrganizerCreateDto;
import br.dev.leandro.spring.event.dto.OrganizerDto;
import br.dev.leandro.spring.event.dto.OrganizerUpdateDto;
import br.dev.leandro.spring.event.entity.Organizer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrganizerMapper {

    Organizer toEntity(OrganizerCreateDto organizerCreateDto);

    OrganizerDto toDto(Organizer organizer);

    OrganizerCreateDto toCreateDto(Organizer organizer);

    void updateEntityFromUpdateDto(OrganizerUpdateDto dto, @MappingTarget Organizer entity);
}
