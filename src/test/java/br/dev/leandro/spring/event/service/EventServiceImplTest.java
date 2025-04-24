package br.dev.leandro.spring.event.service;

import br.dev.leandro.spring.event.controller.dto.EventDto;
import br.dev.leandro.spring.event.entity.Event;
import br.dev.leandro.spring.event.exception.ResourceNotFoundException;
import br.dev.leandro.spring.event.mapper.EventMapper;
import br.dev.leandro.spring.event.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
            verify(eventRepository).save(event);
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenEventNotFound() {
            when(eventRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> eventService.update(1L, eventDto));
        }
    }

    @Nested
    class GetEventTests{

    }

    @Nested
    class DeleteEventTests{
        @Test
        void shouldDeleteExistingEvent() {
            doNothing().when(eventRepository).deleteById(1L);
            eventService.delete(1L);

            verify(eventRepository).deleteById(1L);
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenEventNotFound() {
            when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        }
    }

    @Test
    void getById() {
    }

    @Test
    void getAll() {
    }

    @Test
    void delete() {
    }
}