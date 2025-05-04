package br.dev.leandro.spring.event.unit.service;

import br.dev.leandro.spring.event.controller.dto.OrganizerDto;
import br.dev.leandro.spring.event.entity.*;
import br.dev.leandro.spring.event.exception.ResourceNotFoundException;
import br.dev.leandro.spring.event.mapper.OrganizerMapper;
import br.dev.leandro.spring.event.repository.OrganizerRepository;
import br.dev.leandro.spring.event.service.OrganizerServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class OrganizerServiceImplTest {

    @Mock
    private OrganizerRepository organizerRepository;

    @Mock
    private OrganizerMapper organizerMapper;

    @InjectMocks
    private OrganizerServiceImpl organizerService;

    private Organizer organizer;
    private OrganizerDto organizerDto;
    private User user;
    private Event event;

    @BeforeEach
    void setUp() {
        // Criar Usuário
        user = User.builder()
                .id(1L)
                .name("Usuário de Teste")
                .email("test@example.com")
                .password("password")
                .build();

        // Criar Evento
        event = Event.builder()
                .id(1L)
                .name("Evento de Teste")
                .description("Descrição do Evento de Teste")
                .location("Local de Teste")
                .startDatetime(LocalDateTime.now().plusDays(1))
                .endDatetime(LocalDateTime.now().plusDays(2))
                .status(EventStatus.ACTIVE)
                .build();

        // Criar Organizador
        organizer = new Organizer();
        organizer.setId(1L);
        organizer.setName("Teste de Organizador");
        organizer.setStatus(OrganizerStatus.ACTIVE);
        organizer.setUser(user);
        organizer.setEvents(List.of(event));
        organizer.setRole(OrganizerRole.COLLABORATOR);

        // Criar OrganizerDto
        organizerDto = new OrganizerDto(1L, user, List.of(event), OrganizerRole.COLLABORATOR);
    }

    @Nested
    @DisplayName("Testes de Criação de Organizador")
    class CreateOrganizerTests {

        @Test
        @DisplayName("Deve criar um organizador com sucesso")
        void shouldCreateOrganizer() {
            // Dado
            when(organizerMapper.toEntity(organizerDto)).thenReturn(organizer);
            when(organizerRepository.save(organizer)).thenReturn(organizer);

            // Quando
            Organizer result = organizerService.create(organizerDto);

            // Então
            assertNotNull(result, "O resultado não deve ser nulo");
            assertEquals(organizer.getId(), result.getId(), "O ID deve corresponder");
            assertEquals(organizer.getName(), result.getName(), "O nome deve corresponder");
            assertEquals(organizer.getStatus(), result.getStatus(), "O status deve corresponder");
            assertEquals(organizer.getUser(), result.getUser(), "O usuário deve corresponder");
            assertEquals(organizer.getEvents(), result.getEvents(), "Os eventos devem corresponder");
            assertEquals(organizer.getRole(), result.getRole(), "O papel deve corresponder");

            // Verificar interações
            verify(organizerMapper, times(1)).toEntity(organizerDto);
            verify(organizerRepository, times(1)).save(organizer);
            verifyNoMoreInteractions(organizerMapper, organizerRepository);
        }

        @Test
        @DisplayName("Deve lidar com entrada nula ao criar organizador")
        void shouldHandleNullInput() {
            // Dado
            OrganizerDto nullDto = null;

            // Quando
            // A implementação atual não valida entrada nula no método create
            // Este teste verifica esse comportamento simulando as interações esperadas
            when(organizerMapper.toEntity(null)).thenReturn(null);
            when(organizerRepository.save(null)).thenReturn(null);

            // Então
            assertDoesNotThrow(() -> organizerService.create(nullDto),
                    "Não deve lançar exceção quando a entrada é nula (comportamento atual da implementação)");

            // Verificar interações
            verify(organizerMapper, times(1)).toEntity(null);
            verify(organizerRepository, times(1)).save(null);
        }
    }

    @Nested
    @DisplayName("Testes de Atualização de Organizador")
    class UpdateOrganizerTests {

        @Test
        @DisplayName("Deve atualizar um organizador existente com sucesso")
        void shouldUpdateExistingOrganizer() {
            // Dado
            when(organizerRepository.findByIdAndStatus(1L, OrganizerStatus.ACTIVE)).thenReturn(Optional.of(organizer));
            doNothing().when(organizerMapper).updateEntityFromDto(organizerDto, organizer);
            when(organizerRepository.save(organizer)).thenReturn(organizer);

            // Quando
            Organizer result = organizerService.update(1L, organizerDto);

            // Então
            assertNotNull(result, "O resultado não deve ser nulo");
            assertEquals(organizer.getId(), result.getId(), "O ID deve corresponder");
            assertEquals(organizer.getName(), result.getName(), "O nome deve corresponder");
            assertEquals(organizer.getStatus(), result.getStatus(), "O status deve corresponder");
            assertEquals(organizer.getUser(), result.getUser(), "O usuário deve corresponder");
            assertEquals(organizer.getEvents(), result.getEvents(), "Os eventos devem corresponder");
            assertEquals(organizer.getRole(), result.getRole(), "O papel deve corresponder");

            // Verificar interações
            verify(organizerRepository, times(1)).findByIdAndStatus(1L, OrganizerStatus.ACTIVE);
            verify(organizerMapper, times(1)).updateEntityFromDto(organizerDto, organizer);
            verify(organizerRepository, times(1)).save(organizer);
            verifyNoMoreInteractions(organizerMapper, organizerRepository);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao atualizar um organizador inexistente")
        void shouldThrowWhenUpdatingNonExistentOrganizer() {
            // Dado
            when(organizerRepository.findByIdAndStatus(1L, OrganizerStatus.ACTIVE)).thenReturn(Optional.empty());

            // Quando/Então
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> organizerService.update(1L, organizerDto),
                    "Deve lançar ResourceNotFoundException quando o organizador não existe");

            assertEquals("Organizador não encontrado!", exception.getMessage(), "A mensagem de exceção deve corresponder");

            // Verificar interações
            verify(organizerRepository, times(1)).findByIdAndStatus(1L, OrganizerStatus.ACTIVE);
            verifyNoMoreInteractions(organizerRepository);
            verifyNoInteractions(organizerMapper);
        }

        @Test
        @DisplayName("Deve lidar com entrada nula ao atualizar organizador")
        void shouldHandleNullInput() {
            // Dado
            OrganizerDto nullDto = null;
            when(organizerRepository.findByIdAndStatus(1L, OrganizerStatus.ACTIVE)).thenReturn(Optional.empty());

            // Quando/Então
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                    () -> organizerService.update(1L, nullDto),
                    "Deve lançar ResourceNotFoundException quando a entrada é nula (comportamento atual da implementação)");

            assertEquals("Organizador não encontrado!", exception.getMessage(), "A mensagem de exceção deve corresponder");

            // Verificar interações
            verify(organizerRepository, times(1)).findByIdAndStatus(1L, OrganizerStatus.ACTIVE);
            verifyNoMoreInteractions(organizerRepository);
            verifyNoInteractions(organizerMapper);
        }
    }

    @Nested
    @DisplayName("Testes de Obtenção de Organizador")
    class GetOrganizerTests {

        @Test
        @DisplayName("Deve retornar organizador por ID com sucesso")
        void shouldReturnOrganizerById() {
            // Dado
            when(organizerRepository.findByIdAndStatus(1L, OrganizerStatus.ACTIVE)).thenReturn(Optional.of(organizer));
            when(organizerMapper.toDto(organizer)).thenReturn(organizerDto);

            // Quando
            OrganizerDto result = organizerService.getById(1L);

            // Então
            assertNotNull(result, "O resultado não deve ser nulo");
            assertEquals(organizerDto.id(), result.id(), "O ID deve corresponder");
            assertEquals(organizerDto.user(), result.user(), "O usuário deve corresponder");
            assertEquals(organizerDto.events(), result.events(), "Os eventos devem corresponder");
            assertEquals(organizerDto.role(), result.role(), "O papel deve corresponder");

            // Verificar interações
            verify(organizerRepository, times(1)).findByIdAndStatus(1L, OrganizerStatus.ACTIVE);
            verify(organizerMapper, times(1)).toDto(organizer);
            verifyNoMoreInteractions(organizerMapper, organizerRepository);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando organizador não for encontrado pelo ID")
        void shouldThrowWhenOrganizerNotFoundById() {
            // Dado
            when(organizerRepository.findByIdAndStatus(1L, OrganizerStatus.ACTIVE)).thenReturn(Optional.empty());

            // Quando/Então
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                    () -> organizerService.getById(1L),
                    "Deve lançar ResourceNotFoundException quando o organizador não existe");

            assertEquals("Organizador não encontrado!", exception.getMessage(), "A mensagem de exceção deve corresponder");

            // Verificar interações
            verify(organizerRepository, times(1)).findByIdAndStatus(1L, OrganizerStatus.ACTIVE);
            verifyNoMoreInteractions(organizerRepository);
            verifyNoInteractions(organizerMapper);
        }

        @Test
        @DisplayName("Deve lidar com ID nulo ao obter organizador")
        void shouldHandleNullId() {
            // Dado
            when(organizerRepository.findByIdAndStatus(null, OrganizerStatus.ACTIVE)).thenReturn(Optional.empty());

            // Quando/Então
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                    () -> organizerService.getById(null),
                    "Deve lançar ResourceNotFoundException quando o ID é nulo (comportamento atual da implementação)");

            assertEquals("Organizador não encontrado!", exception.getMessage(), "A mensagem de exceção deve corresponder");

            // Verificar interações
            verify(organizerRepository, times(1)).findByIdAndStatus(null, OrganizerStatus.ACTIVE);
            verifyNoMoreInteractions(organizerRepository);
            verifyNoInteractions(organizerMapper);
        }
    }

    @Nested
    @DisplayName("Testes de Obtenção de Todos os Organizadores")
    class GetAllOrganizersTests {

        @Test
        @DisplayName("Deve retornar todos os organizadores com paginação")
        void shouldReturnAllOrganizersWithPagination() {
            // Dado
            Pageable pageable = PageRequest.of(0, 10);
            Page<Organizer> organizerPage = new PageImpl<>(List.of(organizer));

            when(organizerRepository.findAllByStatus(OrganizerStatus.ACTIVE, pageable)).thenReturn(organizerPage);
            when(organizerMapper.toDto(organizer)).thenReturn(organizerDto);

            // Quando
            Page<OrganizerDto> result = organizerService.getAll(pageable);

            // Então
            assertNotNull(result, "O resultado não deve ser nulo");
            assertEquals(1, result.getContent().size(), "A página deve conter 1 item");
            assertEquals(organizerDto, result.getContent().get(0), "O primeiro item deve corresponder ao organizerDto");
            assertEquals(0, result.getNumber(), "O número da página deve ser 0");
            assertEquals(1, result.getTotalElements(), "O total de elementos deve ser 1");

            // Verificar interações
            verify(organizerRepository, times(1)).findAllByStatus(OrganizerStatus.ACTIVE, pageable);
            verify(organizerMapper, times(1)).toDto(organizer);
            verifyNoMoreInteractions(organizerMapper, organizerRepository);
        }

        @Test
        @DisplayName("Deve retornar página vazia quando não existem organizadores")
        void shouldReturnEmptyPage() {
            // Dado
            Pageable pageable = PageRequest.of(0, 10);
            Page<Organizer> emptyPage = new PageImpl<>(List.of());

            when(organizerRepository.findAllByStatus(OrganizerStatus.ACTIVE, pageable)).thenReturn(emptyPage);

            // Quando
            Page<OrganizerDto> result = organizerService.getAll(pageable);

            // Então
            assertNotNull(result, "O resultado não deve ser nulo");
            assertTrue(result.isEmpty(), "A página deve estar vazia");
            assertEquals(0, result.getTotalElements(), "O total de elementos deve ser 0");

            // Verificar interações
            verify(organizerRepository, times(1)).findAllByStatus(OrganizerStatus.ACTIVE, pageable);
            verifyNoMoreInteractions(organizerRepository);
            verifyNoInteractions(organizerMapper);
        }

        @Test
        @DisplayName("Deve lidar com parâmetro pageable nulo")
        void shouldHandleNullPageable() {
            // Dado
            when(organizerRepository.findAllByStatus(eq(OrganizerStatus.ACTIVE), isNull())).thenReturn(null);

            // Quando/Então
            NullPointerException exception = assertThrows(NullPointerException.class, 
                    () -> organizerService.getAll(null),
                    "Deve lançar NullPointerException quando pageable é nulo (comportamento atual da implementação)");

            assertTrue(exception.getMessage().contains("Cannot invoke \"org.springframework.data.domain.Page.map(java.util.function.Function)\""), 
                    "A mensagem de exceção deve indicar invocação nula de Page.map");

            // Verificar interações
            verify(organizerRepository, times(1)).findAllByStatus(eq(OrganizerStatus.ACTIVE), isNull());
            verifyNoMoreInteractions(organizerRepository);
            verifyNoInteractions(organizerMapper);
        }
    }

    @Nested
    @DisplayName("Testes de Exclusão de Organizador")
    class DeleteOrganizerTests {

        @Test
        @DisplayName("Deve excluir logicamente um organizador")
        void shouldSoftDeleteOrganizer() {
            // Dado
            when(organizerRepository.findByIdAndStatus(1L, OrganizerStatus.ACTIVE)).thenReturn(Optional.of(organizer));
            when(organizerRepository.save(organizer)).thenReturn(organizer);

            // Quando
            organizerService.delete(1L);

            // Então
            assertEquals(OrganizerStatus.DELETED, organizer.getStatus(), "O status do organizador deve ser DELETED");

            // Verificar interações
            verify(organizerRepository, times(1)).findByIdAndStatus(1L, OrganizerStatus.ACTIVE);
            verify(organizerRepository, times(1)).save(organizer);
            verifyNoMoreInteractions(organizerRepository);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao excluir um organizador inexistente")
        void shouldThrowWhenDeletingNonExistentOrganizer() {
            // Dado
            when(organizerRepository.findByIdAndStatus(1L, OrganizerStatus.ACTIVE)).thenReturn(Optional.empty());

            // Quando/Então
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                    () -> organizerService.delete(1L),
                    "Deve lançar ResourceNotFoundException quando o organizador não existe");

            assertEquals("Organizador não encontrado!", exception.getMessage(), "A mensagem de exceção deve corresponder");

            // Verificar interações
            verify(organizerRepository, times(1)).findByIdAndStatus(1L, OrganizerStatus.ACTIVE);
            verifyNoMoreInteractions(organizerRepository);
        }

        @Test
        @DisplayName("Deve lidar com ID nulo ao excluir organizador")
        void shouldHandleNullId() {
            // Dado
            when(organizerRepository.findByIdAndStatus(null, OrganizerStatus.ACTIVE)).thenReturn(Optional.empty());

            // Quando/Então
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                    () -> organizerService.delete(null),
                    "Deve lançar ResourceNotFoundException quando o ID é nulo (comportamento atual da implementação)");

            assertEquals("Organizador não encontrado!", exception.getMessage(), "A mensagem de exceção deve corresponder");

            // Verificar interações
            verify(organizerRepository, times(1)).findByIdAndStatus(null, OrganizerStatus.ACTIVE);
            verifyNoMoreInteractions(organizerRepository);
        }
    }
}
