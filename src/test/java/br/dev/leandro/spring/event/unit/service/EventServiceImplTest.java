package br.dev.leandro.spring.event.unit.service;

import br.dev.leandro.spring.event.dto.EventDto;
import br.dev.leandro.spring.event.entity.Event;
import br.dev.leandro.spring.event.entity.enums.EventStatus;
import br.dev.leandro.spring.event.exception.ResourceNotFoundException;
import br.dev.leandro.spring.event.mapper.EventMapper;
import br.dev.leandro.spring.event.repository.EventRepository;
import br.dev.leandro.spring.event.service.EventServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Eventos")
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventServiceImpl eventService;

    private EventDto eventDto;
    private Event event;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Configurar datas
        startDateTime = LocalDateTime.now().plusDays(1);
        endDateTime = LocalDateTime.now().plusDays(2);

        // Criar DTO de evento
        eventDto = new EventDto(
                UUID.randomUUID(),
                "Evento Teste", 
                "Descrição detalhada do evento", 
                startDateTime, 
                endDateTime, 
                "Local do Evento",
                EventStatus.ACTIVE
        );

        // Criar entidade de evento
        event = Event.builder()
                .id(UUID.randomUUID())
                .name("Evento Teste")
                .description("Descrição detalhada do evento")
                .location("Local do Evento")
                .startDatetime(startDateTime)
                .endDatetime(endDateTime)
                .status(EventStatus.ACTIVE)
                .organizerId(UUID.fromString("af6dbc91-2458-49c4-9708-73fa9cb7317c"))
                .ticketTypes(new ArrayList<>())
                .createdBy("user-id-123")
                .build();

        Jwt jwtMock = Mockito.mock(Jwt.class);
        Mockito.when(jwtMock.getClaim("preferred_username")).thenReturn("usuario-teste");

        // Prepara o Authentication e o coloca no contexto do Spring Security
        Authentication authentication = new TestingAuthenticationToken(jwtMock, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void teardown() {
        SecurityContextHolder.clearContext();
    }


    @Nested
    @DisplayName("Testes de Criação de Evento")
    class CreateEventTests {

//        @Test
        @DisplayName("Deve criar um novo evento com sucesso")
        void shouldCreateNewEvent() {
            // Dado
            when(eventMapper.toEntity(eventDto)).thenReturn(event);
            when(eventRepository.save(event)).thenReturn(event);

            // Quando
            Event result = eventService.create("222", eventDto);

            // Então
            assertNotNull(result, "O resultado não deve ser nulo");
            assertEquals(event.getId(), result.getId(), "O ID deve corresponder");
            assertEquals(event.getName(), result.getName(), "O nome deve corresponder");
            assertEquals(event.getDescription(), result.getDescription(), "A descrição deve corresponder");
            assertEquals(event.getLocation(), result.getLocation(), "O local deve corresponder");
            assertEquals(event.getStartDatetime(), result.getStartDatetime(), "A data de início deve corresponder");
            assertEquals(event.getEndDatetime(), result.getEndDatetime(), "A data de término deve corresponder");
            assertEquals(event.getStatus(), result.getStatus(), "O status deve corresponder");

            // Verificar interações
            verify(eventMapper, times(1)).toEntity(eventDto);
            verify(eventRepository, times(1)).save(event);
            verifyNoMoreInteractions(eventMapper, eventRepository);
        }

        @Test
        @DisplayName("Deve lidar com entrada nula ao criar evento")
        void shouldHandleNullInput() {
            // Dado
            EventDto nullDto = null;

            // A implementação atual não valida entrada nula no método create
            // Este teste verifica esse comportamento simulando as interações esperadas
            when(eventMapper.toEntity(null)).thenReturn(null);
            when(eventRepository.save(null)).thenReturn(null);

            // Quando/Então
            assertDoesNotThrow(() -> eventService.create(null, nullDto),
                    "Não deve lançar exceção quando a entrada é nula (comportamento atual da implementação)");

            // Verificar interações
            verify(eventMapper, times(1)).toEntity(null);
            verify(eventRepository, times(1)).save(null);
        }
    }

    @Nested
    @DisplayName("Testes de Atualização de Evento")
    class UpdateEventTests {
        @Test
        @DisplayName("Deve atualizar um evento existente com sucesso")
        void shouldUpdateExistingEvent() {
            // Dado
            when(eventRepository.findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), EventStatus.ACTIVE)).thenReturn(Optional.of(event));
            doNothing().when(eventMapper).updateEntityFromDto(eventDto, event);
            when(eventRepository.save(event)).thenReturn(event);

            // Quando
            Event result = eventService.update(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), eventDto);

            // Então
            assertNotNull(result, "O resultado não deve ser nulo");
            assertEquals(event.getId(), result.getId(), "O ID deve corresponder");
            assertEquals(event.getName(), result.getName(), "O nome deve corresponder");
            assertEquals(event.getDescription(), result.getDescription(), "A descrição deve corresponder");
            assertEquals(event.getLocation(), result.getLocation(), "O local deve corresponder");
            assertEquals(event.getStartDatetime(), result.getStartDatetime(), "A data de início deve corresponder");
            assertEquals(event.getEndDatetime(), result.getEndDatetime(), "A data de término deve corresponder");
            assertEquals(event.getStatus(), result.getStatus(), "O status deve corresponder");

            // Verificar interações
            verify(eventRepository, times(1)).findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), EventStatus.ACTIVE);
            verify(eventMapper, times(1)).updateEntityFromDto(eventDto, event);
            verify(eventRepository, times(1)).save(event);
            verifyNoMoreInteractions(eventRepository, eventMapper);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao atualizar um evento inexistente")
        void shouldThrowResourceNotFoundExceptionWhenEventNotFound() {
            UUID eventId = UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63");
            when(eventRepository.findByIdAndStatus(eventId, EventStatus.ACTIVE)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> eventService.update(eventId, eventDto));
            assertEquals("Evento não encontrado!", exception.getMessage());

            verify(eventRepository, times(1)).findByIdAndStatus(eventId, EventStatus.ACTIVE);
            verifyNoMoreInteractions(eventRepository);
            verifyNoInteractions(eventMapper);
        }


        @Test
        @DisplayName("Deve lidar com entrada nula ao atualizar evento")
        void shouldHandleNullInput() {
            // Dado
            EventDto nullDto = null;
            when(eventRepository.findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), EventStatus.ACTIVE)).thenReturn(Optional.of(event));
            doNothing().when(eventMapper).updateEntityFromDto(nullDto, event);
            when(eventRepository.save(event)).thenReturn(event);

            // Quando/Então
            assertDoesNotThrow(() -> eventService.update(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), nullDto),
                    "Não deve lançar exceção quando a entrada é nula (comportamento atual da implementação)");

            // Verificar interações
            verify(eventRepository, times(1)).findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), EventStatus.ACTIVE);
            verify(eventMapper, times(1)).updateEntityFromDto(nullDto, event);
            verify(eventRepository, times(1)).save(event);
            verifyNoMoreInteractions(eventRepository, eventMapper);
        }
    }

    @Nested
    @DisplayName("Testes de Obtenção de Evento")
    class GetEventTests {
        @Test
        @DisplayName("Deve retornar EventDto quando o evento for encontrado")
        void shouldReturnEventDtoWhenEventFound() {
            // Dado
            when(eventRepository.findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), EventStatus.ACTIVE)).thenReturn(Optional.of(event));
            when(eventMapper.toDto(event)).thenReturn(eventDto);

            // Quando
            EventDto result = eventService.getById(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"));

            // Então
            assertNotNull(result, "O resultado não deve ser nulo");
            assertEquals(eventDto.id(), result.id(), "O ID deve corresponder");
            assertEquals(eventDto.name(), result.name(), "O nome deve corresponder");
            assertEquals(eventDto.description(), result.description(), "A descrição deve corresponder");
            assertEquals(eventDto.location(), result.location(), "O local deve corresponder");
            assertEquals(eventDto.startDatetime(), result.startDatetime(), "A data de início deve corresponder");
            assertEquals(eventDto.endDatetime(), result.endDatetime(), "A data de término deve corresponder");
            assertEquals(eventDto.status(), result.status(), "O status deve corresponder");

            // Verificar interações
            verify(eventRepository, times(1)).findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), EventStatus.ACTIVE);
            verify(eventMapper, times(1)).toDto(event);
            verifyNoMoreInteractions(eventRepository, eventMapper);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o evento não for encontrado")
        void shouldThrowResourceNotFoundExceptionWhenEventNotFound() {
            // Dado
            when(eventRepository.findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), EventStatus.ACTIVE)).thenReturn(Optional.empty());

            // Quando/Então
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                    () -> eventService.getById(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63")),
                    "Deve lançar ResourceNotFoundException quando o evento não existe");

            assertEquals("Evento não encontrado!", exception.getMessage(), "A mensagem de exceção deve corresponder");

            // Verificar interações
            verify(eventRepository, times(1)).findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), EventStatus.ACTIVE);
            verifyNoMoreInteractions(eventRepository);
            verifyNoInteractions(eventMapper);
        }

        @Test
        @DisplayName("Deve lidar com ID nulo ao obter evento")
        void shouldHandleNullId() {
            // Dado
            when(eventRepository.findByIdAndStatus(null, EventStatus.ACTIVE)).thenReturn(Optional.empty());

            // Quando/Então
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                    () -> eventService.getById(null),
                    "Deve lançar ResourceNotFoundException quando o ID é nulo (comportamento atual da implementação)");

            assertEquals("Evento não encontrado!", exception.getMessage(), "A mensagem de exceção deve corresponder");

            // Verificar interações
            verify(eventRepository, times(1)).findByIdAndStatus(null, EventStatus.ACTIVE);
            verifyNoMoreInteractions(eventRepository);
            verifyNoInteractions(eventMapper);
        }
    }

    @Nested
    @DisplayName("Testes de Obtenção de Todos os Eventos")
    class GetAllEventsTests {

        @Test
        @DisplayName("Deve retornar todos os eventos com paginação")
        void shouldReturnAllEventsWithPagination() {
            // Dado
            Pageable pageable = PageRequest.of(0, 10);
            PageImpl<Event> page = new PageImpl<>(List.of(event));
            when(eventRepository.findAll(pageable)).thenReturn(page);
            when(eventMapper.toDto(event)).thenReturn(eventDto);

            // Quando
            Page<EventDto> result = eventService.getAll(pageable);

            // Então
            assertNotNull(result, "O resultado não deve ser nulo");
            assertEquals(1, result.getContent().size(), "A página deve conter 1 item");
            assertEquals(eventDto, result.getContent().getFirst(), "O primeiro item deve corresponder ao eventDto");
            assertEquals(0, result.getNumber(), "O número da página deve ser 0");
            assertEquals(1, result.getTotalElements(), "O total de elementos deve ser 1");

            // Verificar interações
            verify(eventRepository, times(1)).findAll(pageable);
            verify(eventMapper, times(1)).toDto(event);
            verifyNoMoreInteractions(eventRepository, eventMapper);
        }

        @Test
        @DisplayName("Deve retornar página vazia quando não existem eventos")
        void shouldReturnEmptyPage() {
            // Dado
            Pageable pageable = PageRequest.of(0, 10);
            Page<Event> emptyPage = new PageImpl<>(List.of());
            when(eventRepository.findAll(pageable)).thenReturn(emptyPage);

            // Quando
            Page<EventDto> result = eventService.getAll(pageable);

            // Então
            assertNotNull(result, "O resultado não deve ser nulo");
            assertTrue(result.isEmpty(), "A página deve estar vazia");
            assertEquals(0, result.getTotalElements(), "O total de elementos deve ser 0");

            // Verificar interações
            verify(eventRepository, times(1)).findAll(pageable);
            verifyNoMoreInteractions(eventRepository);
            verifyNoInteractions(eventMapper);
        }

        @Test
        @DisplayName("Deve lidar com parâmetro pageable nulo")
        void shouldHandleNullPageable() {
            // Dado
            Pageable nullPageable = null;

            // Quando/Então
            // Não precisamos mockar o comportamento aqui, pois o NullPointerException
            // ocorrerá naturalmente quando tentarmos chamar findAll com null
            NullPointerException exception = assertThrows(NullPointerException.class, 
                    () -> eventService.getAll(nullPageable),
                    "Deve lançar NullPointerException quando pageable é nulo (comportamento atual da implementação)");

            // Não podemos verificar a mensagem exata pois depende da implementação interna
            // e pode variar entre versões do Java/Spring

            // Não verificamos interações pois o método lança exceção antes de chamar o repository
        }
    }

    @Nested
    @DisplayName("Testes de Exclusão de Evento")
    class DeleteEventTests {
        @Test
        @DisplayName("Deve excluir logicamente um evento")
        void shouldSoftDeleteEvent() {
            // Dado
            when(eventRepository.findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), EventStatus.ACTIVE)).thenReturn(Optional.of(event));
            when(eventRepository.save(event)).thenReturn(event);

            // Quando
            eventService.delete(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"));

            // Então
            assertEquals(EventStatus.DELETED, event.getStatus(), "O status do evento deve ser DELETED");

            // Verificar interações
            verify(eventRepository, times(1)).findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), EventStatus.ACTIVE);
            verify(eventRepository, times(1)).save(event);
            verifyNoMoreInteractions(eventRepository);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao excluir um evento inexistente")
        void shouldThrowWhenDeletingNonExistentEvent() {
            // Dado
            when(eventRepository.findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), EventStatus.ACTIVE)).thenReturn(Optional.empty());

            // Quando/Então
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                    () -> eventService.delete(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63")),
                    "Deve lançar ResourceNotFoundException quando o evento não existe");

            assertEquals("Evento não encontrado!", exception.getMessage(), "A mensagem de exceção deve corresponder");

            // Verificar interações
            verify(eventRepository, times(1)).findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), EventStatus.ACTIVE);
            verifyNoMoreInteractions(eventRepository);
        }

        @Test
        @DisplayName("Deve lidar com ID nulo ao excluir evento")
        void shouldHandleNullId() {
            // Dado
            when(eventRepository.findByIdAndStatus(null, EventStatus.ACTIVE)).thenReturn(Optional.empty());

            // Quando/Então
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                    () -> eventService.delete(null),
                    "Deve lançar ResourceNotFoundException quando o ID é nulo (comportamento atual da implementação)");

            assertEquals("Evento não encontrado!", exception.getMessage(), "A mensagem de exceção deve corresponder");

            // Verificar interações
            verify(eventRepository, times(1)).findByIdAndStatus(null, EventStatus.ACTIVE);
            verifyNoMoreInteractions(eventRepository);
        }
    }
}
