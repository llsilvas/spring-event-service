package br.dev.leandro.spring.event.mapper;

import br.dev.leandro.spring.event.dto.EventDto;
import br.dev.leandro.spring.event.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventMapper eventMapper = Mappers.getMapper(EventMapper.class);

    Event toEntity(EventDto eventDto);

    EventDto toDto(Event event);

    void updateEntityFromDto(EventDto eventDto, @MappingTarget Event event);
}
