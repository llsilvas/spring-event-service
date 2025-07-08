package br.dev.leandro.spring.event.mapper;

import br.dev.leandro.spring.event.dto.OrganizerCreateDto;
import br.dev.leandro.spring.event.dto.OrganizerDto;
import br.dev.leandro.spring.event.dto.OrganizerUpdateDto;
import br.dev.leandro.spring.event.entity.Organizer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring"
)
public interface OrganizerMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true),
            @Mapping(target = "userId", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "createdBy",  ignore = true),
            @Mapping(target = "updatedBy",   ignore = true),
            @Mapping(target = "createdAt",  ignore = true),
            @Mapping(target = "updatedAt", ignore = true)
    })
    Organizer toEntity(OrganizerCreateDto organizerCreateDto);

    OrganizerDto toDto(Organizer organizer);

    OrganizerCreateDto toCreateDto(Organizer organizer);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true),
            @Mapping(target = "userId", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "createdBy",  ignore = true),
            @Mapping(target = "updatedBy",   ignore = true),
            @Mapping(target = "createdAt",  ignore = true),
            @Mapping(target = "updatedAt", ignore = true)
    })
    void updateEntityFromUpdateDto(OrganizerUpdateDto dto, @MappingTarget Organizer entity);
}
