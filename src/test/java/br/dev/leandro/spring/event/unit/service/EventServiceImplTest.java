package br.dev.leandro.spring.event.unit.service;

import br.dev.leandro.spring.event.controller.dto.EventDto;
import br.dev.leandro.spring.event.entity.Event;
import br.dev.leandro.spring.event.entity.EventStatus;
import br.dev.leandro.spring.event.exception.ResourceNotFoundException;
import br.dev.leandro.spring.event.mapper.EventMapper;
import br.dev.leandro.spring.event.repository.EventRepository;
import br.dev.leandro.spring.event.service.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Tag("unit")
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventServiceImpl eventService;

    private EventDto eventDto;
    private Event event;

    @BeforeEach
    void setUp() {
        eventDto = new EventDto(1L, "Evento Teste", "Descrição", null, null, "Local", null);
        event = new Event();
        event.setId(1L);
        event.setName("Evento Teste");
        event.setDescription("Descrição");
        event.setLocation("Local");
    }


    @Nested
    class CreateEventTests{

        @Test
        void shouldCreateNewEvent() {
            Mockito.when(eventMapper.toEntity(eventDto)).thenReturn(event);
            Mockito.when(eventRepository.save(event)).thenReturn(event);

            Event result = eventService.create(eventDto);

            assertEquals(event, result);

            verify(eventRepository).save(event);
            verifyNoMoreInteractions(eventRepository);
        }
    }

    @Nested
    class UpdateEventTests{
        @Test
        void shouldUpdateExistingEvent() {
            when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
            doNothing().when(eventMapper).updateEntityFromDto(eventDto, event);
            when(eventRepository.save(event)).thenReturn(event);

            Event result = eventService.update(1L, eventDto);

            assertEquals(event, result);
            verify(eventRepository).findById(1L);
            verify(eventMapper).updateEntityFromDto(eventDto, event);
            verify(eventRepository).save(event);
            verifyNoMoreInteractions(eventRepository);
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenEventNotFound() {
            when(eventRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> eventService.update(1L, eventDto));

            verify(eventRepository).findById(1L);
            verifyNoMoreInteractions(eventRepository);
        }
    }

    @Nested
    class GetEventTests{
        @Test
        void shouldReturnEventDtoWhenEventFound() {
            when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
            when(eventMapper.toDto(event)).thenReturn(eventDto);

            EventDto result = eventService.getById(1L);

            assertEquals(eventDto, result);
            verify(eventRepository).findById(1L);
            verify(eventMapper).toDto(event);
            verifyNoMoreInteractions(eventRepository);
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenEventNotFound() {
            when(eventRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> eventService.getById(1L));

            verify(eventRepository).findById(1L);
            verifyNoMoreInteractions(eventRepository);
        }
    }

    @Nested
    class GetAllEventsTests{

        @Test
        void shouldReturnAllEvents() {
            Pageable pageable = PageRequest.of(0,10);
            PageImpl<Event> page = new PageImpl<>(List.of(event));
            when(eventRepository.findAll(pageable)).thenReturn(page);
            when(eventMapper.toDto(event)).thenReturn(eventDto);

            Page<EventDto> result = eventService.getAll(pageable);

            assertEquals(1, result.getContent().size());
            assertEquals(eventDto, result.getContent().getFirst());
            verify(eventRepository).findAll(pageable);
            verify(eventMapper).toDto(event);
            verifyNoMoreInteractions(eventRepository);

        }
    }

    @Nested
    class DeleteEventTests{
        @Test
        void shouldDeleteExistingEvent() {
            when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
            when(eventRepository.save(event)).thenReturn(event);
            eventService.delete(1L);
            assertEquals(EventStatus.DELETED, event.getStatus());
            verify(eventRepository).save(event);
            verifyNoMoreInteractions(eventRepository);
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenEventNotFound() {
            when(eventRepository.findById(1L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> eventService.delete(1L));
            verify(eventRepository).findById(1L);
            verifyNoMoreInteractions(eventRepository);
        }
    }
}