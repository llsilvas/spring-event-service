package br.dev.leandro.spring.event.controller;

import br.dev.leandro.spring.event.controller.dto.OrganizerDto;
import br.dev.leandro.spring.event.entity.Organizer;
import br.dev.leandro.spring.event.mapper.OrganizerMapper;
import br.dev.leandro.spring.event.service.OrganizerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller para gerenciamento de organizadores de eventos.
 */
@Slf4j
@RestController
@RequestMapping("/organizers")
@Tag(name = "Organizadores", description = "API para gerenciamento de organizadores de eventos")
@CacheConfig(cacheNames = "organizers")
public class OrganizerController {
    /**
     * Serviço de organizadores.
     */
    private final OrganizerService organizerService;

    /**
     * Mapeador de organizadores.
     */
    private final OrganizerMapper organizerMapper;

    /**
     * Construtor.
     *
     * @param organizerServiceParam Serviço de organizadores
     * @param organizerMapperParam Mapeador de organizadores
     */
    public OrganizerController(final OrganizerService organizerServiceParam, 
            final OrganizerMapper organizerMapperParam) {
        this.organizerService = organizerServiceParam;
        this.organizerMapper = organizerMapperParam;
    }

    /**
     * Cria um novo organizador.
     *
     * @param organizerDto Dados do organizador a ser criado
     * @return Organizador criado
     */
    @PostMapping
    @Operation(summary = "Criar organizador", description = "Cria um novo organizador")
    @ApiResponse(responseCode = "201", description = "Organizador criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
    @CacheEvict(allEntries = true)
    public ResponseEntity<OrganizerDto> createOrganizer(
            @Valid @RequestBody final OrganizerDto organizerDto) {
        log.info("Criando novo organizador para os eventos: {}", 
                organizerDto.events() != null ? organizerDto.events().stream().map(e -> e.getId().toString()).reduce((a, b) -> a + ", " + b).orElse("N/A") : "N/A");
        Organizer organizer = organizerService.create(organizerDto);
        log.info("Organizador criado com ID: {}", organizer.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(organizerMapper.toDto(organizer));
    }

    /**
     * Atualiza um organizador existente.
     *
     * @param id ID do organizador a ser atualizado
     * @param organizerDto Novos dados do organizador
     * @return Organizador atualizado
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar organizador", 
            description = "Atualiza um organizador existente")
    @ApiResponse(responseCode = "200", 
            description = "Organizador atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Organizador não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
    @CacheEvict(allEntries = true)
    public ResponseEntity<OrganizerDto> updateOrganizer(
            @Parameter(description = "ID do organizador") @PathVariable final Long id,
            @Valid @RequestBody final OrganizerDto organizerDto) {
        log.info("Atualizando organizador com ID: {}", id);
        Organizer organizer = organizerService.update(id, organizerDto);
        log.info("Organizador atualizado: {}", organizer.getId());
        return ResponseEntity.ok(organizerMapper.toDto(organizer));
    }

    /**
     * Busca um organizador pelo ID.
     *
     * @param id ID do organizador
     * @return Organizador encontrado
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar organizador por ID", 
            description = "Retorna um organizador pelo ID")
    @ApiResponse(responseCode = "200", description = "Organizador encontrado")
    @ApiResponse(responseCode = "404", description = "Organizador não encontrado")
    @Cacheable(key = "#id")
    public ResponseEntity<OrganizerDto> getOrganizerById(
            @Parameter(description = "ID do organizador") 
            @PathVariable final Long id) {
        log.info("Buscando organizador com ID: {}", id);
        OrganizerDto organizerDto = organizerService.getById(id);
        return ResponseEntity.ok(organizerDto);
    }

    /**
     * Lista todos os organizadores com paginação.
     *
     * @param pageable Informações de paginação
     * @return Lista de organizadores
     */
    @GetMapping
    @Operation(summary = "Listar organizadores", 
            description = "Lista todos os organizadores com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de organizadores")
    @Cacheable
    public ResponseEntity<Iterable<OrganizerDto>> listOrganizers(final Pageable pageable) {
        log.info("Listando organizadores. Page: {}, Size: {}", 
                pageable.getPageNumber(), pageable.getPageSize());
        Iterable<OrganizerDto> organizerDtos = organizerService.getAll(pageable);
        return ResponseEntity.ok(organizerDtos);
    }

    /**
     * Remove um organizador.
     *
     * @param id ID do organizador a ser removido
     * @return Sem conteúdo
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Remover organizador", 
            description = "Remove um organizador")
    @ApiResponse(responseCode = "204", 
            description = "Organizador removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Organizador não encontrado")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
    @CacheEvict(allEntries = true)
    public ResponseEntity<Void> deleteOrganizer(
            @Parameter(description = "ID do organizador") 
            @PathVariable final Long id) {
        log.info("Removendo organizador com ID: {}", id);
        organizerService.delete(id);
        log.info("Organizador removido: {}", id);
        return ResponseEntity.noContent().build();
    }
}
