package br.dev.leandro.spring.event.mapper;

import br.dev.leandro.spring.event.dto.EventDto;
import br.dev.leandro.spring.event.entity.Event;
import org.mapstruct.*;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface EventMapper {

    @Mappings({
            @Mapping(target = "organizerId", ignore = true),
            @Mapping(target = "ticketTypes", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "updatedBy", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true)
    })
    Event toEntity(EventDto eventDto);

    EventDto toDto(Event event);

    @Mappings({
            @Mapping(target = "organizerId", ignore = true),
            @Mapping(target = "ticketTypes", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "updatedBy", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true)
    })
    void updateEntityFromDto(EventDto eventDto, @MappingTarget Event event);
}
