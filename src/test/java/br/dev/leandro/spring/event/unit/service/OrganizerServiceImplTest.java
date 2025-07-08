package br.dev.leandro.spring.event.unit.service;

import br.dev.leandro.spring.event.dto.OrganizerCreateDto;
import br.dev.leandro.spring.event.dto.OrganizerDto;
import br.dev.leandro.spring.event.dto.OrganizerUpdateDto;
import br.dev.leandro.spring.event.entity.Organizer;
import br.dev.leandro.spring.event.entity.enums.OrganizerStatus;
import br.dev.leandro.spring.event.exception.ResourceNotFoundException;
import br.dev.leandro.spring.event.mapper.OrganizerMapper;
import br.dev.leandro.spring.event.repository.OrganizerRepository;
import br.dev.leandro.spring.event.service.OrganizerServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DisplayName("Teste do Serviço de Organizador")
class OrganizerServiceImplTest {

    @Mock
    private OrganizerRepository organizerRepository;

    @Mock
    private OrganizerMapper organizerMapper;

    @InjectMocks
    private OrganizerServiceImpl organizerService;

    private Organizer organizer;
    private OrganizerCreateDto organizerCreateDto;
    private OrganizerUpdateDto organizerUpdateDto;
    private OrganizerDto organizerDto;

    @BeforeEach
    void setUp() {
        // Criar Organizador
        organizer = Organizer.builder()
                .id(UUID.randomUUID())
                .userId("user-uuid-123")
                .organizationName("Teste de Organizador")
                .status(OrganizerStatus.ACTIVE)
                .contactEmail("contato@organizador.com")
                .contactPhone("(11) 99999-9999")
                .documentNumber("12345678901")
                .build();

        // Criar OrganizerDto
        organizerCreateDto = new OrganizerCreateDto( "Teste Eventos", "teste@abceventos.com", "1111-1111", "111111111111-22");
        organizerUpdateDto = new OrganizerUpdateDto("Teste Organization", "teste@teste.com", "11 49449944", "11223344-55", OrganizerStatus.ACTIVE);
        organizerDto = new OrganizerDto(organizer.getUserId(), organizer.getOrganizationName(), organizer.getContactEmail(), organizer.getContactPhone(), organizer.getDocumentNumber(), organizer.getStatus(), organizer.getCreatedBy(), organizer.getUpdatedBy(), organizer.getCreatedAt(), organizer.getUpdatedAt());

        Jwt jwtMock = Mockito.mock(Jwt.class);
        lenient().when(jwtMock.getClaim("preferred_username")).thenReturn("usuario-teste");

        // Prepara o Authentication e o coloca no contexto do Spring Security
        Authentication authentication = new TestingAuthenticationToken(jwtMock, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Nested
    @DisplayName("Testes de Criação de Organizador")
    class CreateOrganizerTests {

        @Test
        @DisplayName("Deve criar um organizador com sucesso")
        void shouldCreateOrganizer() {
            // Dado
            when(organizerRepository.existsByUserId("111")).thenReturn(false);
            when(organizerMapper.toEntity(organizerCreateDto)).thenReturn(organizer);
            when(organizerRepository.save(organizer)).thenReturn(organizer);

            // Quando
            Organizer result = organizerService.create("111", organizerCreateDto);

            // Então
            assertNotNull(result, "O resultado não deve ser nulo");
            assertEquals(organizer.getId(), result.getId(), "O ID deve corresponder");
            assertEquals(organizer.getOrganizationName(), result.getOrganizationName(), "O nome da organização deve corresponder");
            assertEquals(organizer.getStatus(), result.getStatus(), "O status deve corresponder");
            assertEquals(organizer.getUserId(), result.getUserId(), "O ID do usuário deve corresponder");
            assertEquals(organizer.getContactEmail(), result.getContactEmail(), "O email de contato deve corresponder");
            assertEquals(organizer.getContactPhone(), result.getContactPhone(), "O telefone de contato deve corresponder");
            assertEquals(organizer.getDocumentNumber(), result.getDocumentNumber(), "O número do documento deve corresponder");

            // Verificar interações
            verify(organizerRepository, times(1)).existsByUserId("111");
            verify(organizerMapper, times(1)).toEntity(organizerCreateDto);
            verify(organizerRepository, times(1)).save(organizer);
            verifyNoMoreInteractions(organizerMapper, organizerRepository);
        }

        @Test
        @DisplayName("Deve lidar com entrada nula ao criar organizador")
        void shouldHandleNullInput() {
            // Dado
            OrganizerCreateDto nullDto = null;
            String userId = "af6dbc91-2458-49c4-9708-73fa9cb7317c";

            // Então
            assertThrows(IllegalArgumentException.class,
                    () -> organizerService.create(userId, nullDto),
                    "OrganizerCreateDto não pode ser nulo.");

        }
    }

    @Nested
    @DisplayName("Testes de Atualização de Organizador")
    class UpdateOrganizerTests {
        // Nota: O teste para verificar se um admin pode atualizar um organizador mesmo não sendo o proprietário
        // não está incluído devido à complexidade de mockar o SecurityContextHolder.
        // Isso seria mais adequado para um teste de integração onde o contexto de segurança pode ser configurado corretamente.

        @Test
        @DisplayName("Deve atualizar um organizador existente com sucesso quando o usuário é o proprietário")
        void shouldUpdateExistingOrganizer() {
            // Dado
            when(organizerRepository.findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), OrganizerStatus.ACTIVE)).thenReturn(Optional.of(organizer));
            doNothing().when(organizerMapper).updateEntityFromUpdateDto(organizerUpdateDto, organizer);
            when(organizerRepository.save(organizer)).thenReturn(organizer);

            // Quando
            Organizer result = organizerService.update(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), organizerUpdateDto, "user-uuid-123");

            // Então
            assertNotNull(result, "O resultado não deve ser nulo");
            assertEquals(organizer.getId(), result.getId(), "O ID deve corresponder");
            assertEquals(organizer.getOrganizationName(), result.getOrganizationName(), "O nome da organização deve corresponder");
            assertEquals(organizer.getStatus(), result.getStatus(), "O status deve corresponder");
            assertEquals(organizer.getUserId(), result.getUserId(), "O ID do usuário deve corresponder");
            assertEquals(organizer.getContactEmail(), result.getContactEmail(), "O email de contato deve corresponder");
            assertEquals(organizer.getContactPhone(), result.getContactPhone(), "O telefone de contato deve corresponder");
            assertEquals(organizer.getDocumentNumber(), result.getDocumentNumber(), "O número do documento deve corresponder");

            // Verificar interações
            verify(organizerRepository, times(1)).findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), OrganizerStatus.ACTIVE);
            verify(organizerMapper, times(1)).updateEntityFromUpdateDto(organizerUpdateDto, organizer);
            verify(organizerRepository, times(1)).save(organizer);
            verifyNoMoreInteractions(organizerMapper, organizerRepository);
        }

        @Test
        @DisplayName("Deve lançar AccessDeniedException ao atualizar um organizador por um usuário não autorizado")
        void shouldThrowAccessDeniedExceptionWhenUnauthorizedUser() {
            // Dado
            UUID organizerId = UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63");
            String unauthorizedUserId = "different-user-id";
            when(organizerRepository.findByIdAndStatus(organizerId, OrganizerStatus.ACTIVE)).thenReturn(Optional.of(organizer));

            // Simula um usuário autenticado que não é admin
            Jwt jwt = Jwt.withTokenValue("token")
                    .header("alg", "none")
                    .claim("preferred_username", "different-user")
                    .build();
            Authentication authentication = new TestingAuthenticationToken(jwt, null, "ROLE_USER");
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Quando/Então
            assertThrows(org.springframework.security.access.AccessDeniedException.class,
                    () -> organizerService.update(organizerId, organizerUpdateDto, unauthorizedUserId),
                    "Deve lançar AccessDeniedException quando o usuário não é o proprietário nem admin");

            // Verificar interações
            verify(organizerRepository, times(1)).findByIdAndStatus(organizerId, OrganizerStatus.ACTIVE);
            verifyNoMoreInteractions(organizerRepository);
            verifyNoInteractions(organizerMapper);
        }


        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao atualizar um organizador inexistente")
        void shouldThrowWhenUpdatingNonExistentOrganizer() {
            // Dado
            when(organizerRepository.findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), OrganizerStatus.ACTIVE)).thenReturn(Optional.empty());

            // Quando/Então
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> organizerService.update(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), organizerUpdateDto, "user-uuid-123"),
                    "Deve lançar ResourceNotFoundException quando o organizador não existe");

            assertEquals("Organizador não encontrado!", exception.getMessage(), "A mensagem de exceção deve corresponder");

            // Verificar interações
            verify(organizerRepository, times(1)).findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), OrganizerStatus.ACTIVE);
            verifyNoMoreInteractions(organizerRepository);
            verifyNoInteractions(organizerMapper);
        }

        @Test
        @DisplayName("Deve lidar com entrada nula ao atualizar organizador")
        void shouldHandleNullInput() {
            // Dado
            OrganizerUpdateDto nullDto = null;
            when(organizerRepository.findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), OrganizerStatus.ACTIVE)).thenReturn(Optional.empty());

            // Quando/Então
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> organizerService.update(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), nullDto, "user-uuid-123"),
                    "Deve lançar ResourceNotFoundException quando a entrada é nula (comportamento atual da implementação)");

            assertEquals("Organizador não encontrado!", exception.getMessage(), "A mensagem de exceção deve corresponder");

            // Verificar interações
            verify(organizerRepository, times(1)).findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), OrganizerStatus.ACTIVE);
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
            when(organizerRepository.findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), OrganizerStatus.ACTIVE)).thenReturn(Optional.of(organizer));
            when(organizerMapper.toDto(organizer)).thenReturn(organizerDto);

            // Quando
            OrganizerDto result = organizerService.getById(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"));

            // Então
            assertNotNull(result, "O resultado não deve ser nulo");
            assertEquals(organizerDto.organizationName(), result.organizationName(), "O nome da organização deve corresponder");
            assertEquals(organizerDto.contactEmail(), result.contactEmail(), "O email de contato deve corresponder");
            assertEquals(organizerDto.contactPhone(), result.contactPhone(), "O telefone de contato deve corresponder");
            assertEquals(organizerDto.documentNumber(), result.documentNumber(), "O número do documento deve corresponder");

            // Verificar interações
            verify(organizerRepository, times(1)).findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), OrganizerStatus.ACTIVE);
            verify(organizerMapper, times(1)).toDto(organizer);
            verifyNoMoreInteractions(organizerMapper, organizerRepository);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando organizador não for encontrado pelo ID")
        void shouldThrowWhenOrganizerNotFoundById() {
            // Dado
            when(organizerRepository.findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), OrganizerStatus.ACTIVE)).thenReturn(Optional.empty());

            // Quando/Então
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> organizerService.getById(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63")),
                    "Deve lançar ResourceNotFoundException quando o organizador não existe");

            assertEquals("Organizador não encontrado!", exception.getMessage(), "A mensagem de exceção deve corresponder");

            // Verificar interações
            verify(organizerRepository, times(1)).findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), OrganizerStatus.ACTIVE);
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
        @DisplayName("Deve lançar NullPointerException ao lidar com pageable nulo")
        void shouldHandleNullPageable() {
            // Dado
            Pageable nullPageable = null;
            // Mock para simular o comportamento do repositório que pode retornar null
            // se o pageable for nulo, resultando em um NPE no .map()
            when(organizerRepository.findAllByStatus(OrganizerStatus.ACTIVE, nullPageable)).thenReturn(null);

            // Quando/Então
            assertThrows(NullPointerException.class,
                    () -> organizerService.getAll(nullPageable),
                    "Deve lançar NullPointerException quando pageable é nulo e o repositório retorna null");

            // Verificar interações
            verify(organizerRepository, times(1)).findAllByStatus(OrganizerStatus.ACTIVE, nullPageable);
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
            when(organizerRepository.findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), OrganizerStatus.ACTIVE)).thenReturn(Optional.of(organizer));
            when(organizerRepository.save(organizer)).thenReturn(organizer);

            // Quando
            organizerService.delete(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"));

            // Então
            assertEquals(OrganizerStatus.DELETED, organizer.getStatus(), "O status do organizador deve ser DELETED");

            // Verificar interações
            verify(organizerRepository, times(1)).findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), OrganizerStatus.ACTIVE);
            verify(organizerRepository, times(1)).save(organizer);
            verifyNoMoreInteractions(organizerRepository);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao excluir um organizador inexistente")
        void shouldThrowWhenDeletingNonExistentOrganizer() {
            // Dado
            when(organizerRepository.findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), OrganizerStatus.ACTIVE)).thenReturn(Optional.empty());

            // Quando/Então
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> organizerService.delete(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63")),
                    "Deve lançar ResourceNotFoundException quando o organizador não existe");

            assertEquals("Organizador não encontrado!", exception.getMessage(), "A mensagem de exceção deve corresponder");

            // Verificar interações
            verify(organizerRepository, times(1)).findByIdAndStatus(UUID.fromString("6785e97d-53d1-4be2-9233-3f8cfb549f63"), OrganizerStatus.ACTIVE);
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
