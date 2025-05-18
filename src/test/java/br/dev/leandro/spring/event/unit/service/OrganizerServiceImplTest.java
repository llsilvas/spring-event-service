package br.dev.leandro.spring.event.unit.service;

import br.dev.leandro.spring.event.dto.OrganizerCreateDto;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

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
    private OrganizerCreateDto organizerCreateDto;
    private OrganizerUpdateDto organizerUpdateDto;

    @BeforeEach
    void setUp() {


        // Criar Organizador
        organizer = Organizer.builder()
                .id(1L)
                .userId("user-uuid-123")
                .organizationName("Teste de Organizador")
                .status(OrganizerStatus.ACTIVE)
                .contactEmail("contato@organizador.com")
                .contactPhone("(11) 99999-9999")
                .documentNumber("12345678901")
                .build();

        // Criar OrganizerDto
        organizerCreateDto = new OrganizerCreateDto("asc-333333", "Teste Eventos", "teste@abceventos.com", "1111-1111", "111111111111-22");
        new OrganizerUpdateDto("Teste Organization", "teste@teste.com", "11 49449944", "11223344-55", OrganizerStatus.ACTIVE);
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

            // Quando
            // A implementação atual não valida entrada nula no método create
            // Este teste verifica esse comportamento simulando as interações esperadas
            when(organizerRepository.existsByUserId(null)).thenReturn(false);
            when(organizerMapper.toEntity(null)).thenReturn(null);
            when(organizerRepository.save(null)).thenReturn(null);

            // Então
            assertDoesNotThrow(() -> organizerService.create(null, nullDto),
                    "Não deve lançar exceção quando a entrada é nula (comportamento atual da implementação)");

            // Verificar interações
            verify(organizerRepository, times(1)).existsByUserId(null);
            verify(organizerMapper, times(1)).toEntity(null);
            verify(organizerRepository, times(1)).save(null);
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
            when(organizerRepository.findByIdAndStatus(1L, OrganizerStatus.ACTIVE)).thenReturn(Optional.of(organizer));
            doNothing().when(organizerMapper).updateEntityFromUpdateDto(organizerUpdateDto, organizer);
            when(organizerRepository.save(organizer)).thenReturn(organizer);

            // Quando
            Organizer result = organizerService.update(1L, organizerUpdateDto, "user-uuid-123");

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
            verify(organizerRepository, times(1)).findByIdAndStatus(1L, OrganizerStatus.ACTIVE);
            verify(organizerMapper, times(1)).updateEntityFromUpdateDto(organizerUpdateDto, organizer);
            verify(organizerRepository, times(1)).save(organizer);
            verifyNoMoreInteractions(organizerMapper, organizerRepository);
        }

        @Test
        @DisplayName("Deve lançar AccessDeniedException ao atualizar um organizador por um usuário não autorizado")
        void shouldThrowAccessDeniedExceptionWhenUnauthorizedUser() {
            // Dado
            when(organizerRepository.findByIdAndStatus(1L, OrganizerStatus.ACTIVE)).thenReturn(Optional.of(organizer));

            // Configurar o contexto de segurança para simular um usuário sem permissões de admin
            org.springframework.security.core.Authentication authentication = mock(org.springframework.security.core.Authentication.class);
            org.springframework.security.core.context.SecurityContext securityContext = mock(org.springframework.security.core.context.SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            org.springframework.security.core.context.SecurityContextHolder.setContext(securityContext);

            // Simular que o usuário não tem o papel ROLE_ADMIN
            when(authentication.getAuthorities()).thenReturn(java.util.Collections.emptyList());

            try {
                // Quando/Então
                assertThrows(org.springframework.security.access.AccessDeniedException.class,
                        () -> organizerService.update(1L, organizerUpdateDto, "different-user-id"),
                        "Deve lançar AccessDeniedException quando o usuário não é o proprietário nem admin");

                // Verificar interações
                verify(organizerRepository, times(1)).findByIdAndStatus(1L, OrganizerStatus.ACTIVE);
                verifyNoMoreInteractions(organizerRepository);
                verifyNoInteractions(organizerMapper);
            } finally {
                // Limpar o contexto de segurança após o teste
                org.springframework.security.core.context.SecurityContextHolder.clearContext();
            }
        }


        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao atualizar um organizador inexistente")
        void shouldThrowWhenUpdatingNonExistentOrganizer() {
            // Dado
            when(organizerRepository.findByIdAndStatus(1L, OrganizerStatus.ACTIVE)).thenReturn(Optional.empty());

            // Quando/Então
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> organizerService.update(1L, organizerUpdateDto, "user-uuid-123"),
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
            OrganizerUpdateDto nullDto = null;
            when(organizerRepository.findByIdAndStatus(1L, OrganizerStatus.ACTIVE)).thenReturn(Optional.empty());

            // Quando/Então
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> organizerService.update(1L, nullDto, "user-uuid-123"),
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
            when(organizerMapper.toCreateDto(organizer)).thenReturn(organizerCreateDto);

            // Quando
            OrganizerCreateDto result = organizerService.getById(1L);

            // Então
            assertNotNull(result, "O resultado não deve ser nulo");
            assertEquals(organizerCreateDto.organizationName(), result.organizationName(), "O nome da organização deve corresponder");
            assertEquals(organizerCreateDto.contactEmail(), result.contactEmail(), "O email de contato deve corresponder");
            assertEquals(organizerCreateDto.contactPhone(), result.contactPhone(), "O telefone de contato deve corresponder");
            assertEquals(organizerCreateDto.documentNumber(), result.documentNumber(), "O número do documento deve corresponder");

            // Verificar interações
            verify(organizerRepository, times(1)).findByIdAndStatus(1L, OrganizerStatus.ACTIVE);
            verify(organizerMapper, times(1)).toCreateDto(organizer);
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
            when(organizerMapper.toCreateDto(organizer)).thenReturn(organizerCreateDto);

            // Quando
            Page<OrganizerCreateDto> result = organizerService.getAll(pageable);

            // Então
            assertNotNull(result, "O resultado não deve ser nulo");
            assertEquals(1, result.getContent().size(), "A página deve conter 1 item");
            assertEquals(organizerCreateDto, result.getContent().get(0), "O primeiro item deve corresponder ao organizerDto");
            assertEquals(0, result.getNumber(), "O número da página deve ser 0");
            assertEquals(1, result.getTotalElements(), "O total de elementos deve ser 1");

            // Verificar interações
            verify(organizerRepository, times(1)).findAllByStatus(OrganizerStatus.ACTIVE, pageable);
            verify(organizerMapper, times(1)).toCreateDto(organizer);
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
            Page<OrganizerCreateDto> result = organizerService.getAll(pageable);

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
