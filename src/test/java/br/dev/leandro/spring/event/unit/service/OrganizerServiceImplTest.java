package br.dev.leandro.spring.event.unit.service;

import br.dev.leandro.spring.event.controller.dto.OrganizerDto;
import br.dev.leandro.spring.event.entity.Organizer;
import br.dev.leandro.spring.event.entity.OrganizerRole;
import br.dev.leandro.spring.event.entity.OrganizerStatus;
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

    @BeforeEach
    void setUp() {
        organizer = new Organizer();
        organizer.setId(1L);
        organizer.setName("Organizer Test");
        organizer.setStatus(OrganizerStatus.ACTIVE);

        organizerDto = new OrganizerDto(1L, null, null, OrganizerRole.COLLABORATOR);
    }

    @Nested
    class CreateOrganizerTests {

        @Test
        void shouldCreateOrganizer() {
            when(organizerMapper.toEntity(organizerDto)).thenReturn(organizer);
            when(organizerRepository.save(organizer)).thenReturn(organizer);

            Organizer result = organizerService.create(organizerDto);

            assertEquals(organizer, result);
            verify(organizerRepository).save(organizer);
        }
    }

    @Nested
    class UpdateOrganizerTests {

        @Test
        void shouldUpdateExistingOrganizer() {
            when(organizerRepository.findByIdAndStatus(1L, OrganizerStatus.ACTIVE)).thenReturn(Optional.of(organizer));
            doNothing().when(organizerMapper).updateEntityFromDto(organizerDto, organizer);
            when(organizerRepository.save(organizer)).thenReturn(organizer);

            Organizer result = organizerService.update(1L, organizerDto);

            assertEquals(organizer, result);
            verify(organizerRepository).findByIdAndStatus(1L, OrganizerStatus.ACTIVE);
            verify(organizerMapper).updateEntityFromDto(organizerDto, organizer);
            verify(organizerRepository).save(organizer);
        }

        @Test
        void shouldThrowWhenUpdatingNonExistentOrganizer() {
            when(organizerRepository.findByIdAndStatus(1L, OrganizerStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> organizerService.update(1L, organizerDto));
        }
    }

    @Nested
    class GetOrganizerTests {

        @Test
        void shouldReturnOrganizerById() {
            when(organizerRepository.findByIdAndStatus(1L, OrganizerStatus.ACTIVE)).thenReturn(Optional.of(organizer));
            when(organizerMapper.toDto(organizer)).thenReturn(organizerDto);

            OrganizerDto result = organizerService.getById(1L);

            assertEquals(organizerDto, result);
            verify(organizerRepository).findByIdAndStatus(1L, OrganizerStatus.ACTIVE);
            verify(organizerMapper).toDto(organizer);
        }

        @Test
        void shouldThrowWhenOrganizerNotFoundById() {
            when(organizerRepository.findByIdAndStatus(1L, OrganizerStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> organizerService.getById(1L));
        }
    }

    @Nested
    class GetAllOrganizersTests {

        @Test
        void shouldReturnAllOrganizersWithPagination() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Organizer> organizerPage = new PageImpl<>(List.of(organizer));

            when(organizerRepository.findAllByStatus(OrganizerStatus.ACTIVE, pageable)).thenReturn(organizerPage);
            when(organizerMapper.toDto(organizer)).thenReturn(organizerDto);

            Page<OrganizerDto> result = organizerService.getAll(pageable);

            assertEquals(1, result.getContent().size());
            assertEquals(organizerDto, result.getContent().get(0));
            verify(organizerRepository).findAllByStatus(OrganizerStatus.ACTIVE, pageable);
            verify(organizerMapper).toDto(organizer);
        }
    }

    @Nested
    class DeleteOrganizerTests {

        @Test
        void shouldSoftDeleteOrganizer() {
            when(organizerRepository.findByIdAndStatus(1L, OrganizerStatus.ACTIVE)).thenReturn(Optional.of(organizer));
            when(organizerRepository.save(organizer)).thenReturn(organizer);

            organizerService.delete(1L);

            assertEquals(OrganizerStatus.DELETED, organizer.getStatus());
            verify(organizerRepository).save(organizer);
            verifyNoMoreInteractions(organizerRepository);
        }

        @Test
        void shouldThrowWhenDeletingNonExistentOrganizer() {
            when(organizerRepository.findByIdAndStatus(1L, OrganizerStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> organizerService.delete(1L));
            verify(organizerRepository).findByIdAndStatus(1L, OrganizerStatus.ACTIVE);
            verifyNoMoreInteractions(organizerRepository);
        }
    }
}
