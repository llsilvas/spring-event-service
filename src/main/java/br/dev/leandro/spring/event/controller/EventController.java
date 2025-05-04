package br.dev.leandro.spring.event.controller;

import br.dev.leandro.spring.event.controller.dto.EventDto;
import br.dev.leandro.spring.event.entity.Event;
import br.dev.leandro.spring.event.mapper.EventMapper;
import br.dev.leandro.spring.event.service.EventService;
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
 * Controller para gerenciamento de eventos.
 */
@Slf4j
@RestController
@RequestMapping("/events")
@Tag(name = "Eventos", description = "API para gerenciamento de eventos")
@CacheConfig(cacheNames = "events")
public class EventController {
    /**
     * Serviço de eventos.
     */
    private final EventService eventService;

    /**
     * Mapeador de eventos.
     */
    private final EventMapper eventMapper;

    /**
     * Construtor.
     *
     * @param eventServiceParam Serviço de eventos
     * @param eventMapperParam Mapeador de eventos
     */
    public EventController(final EventService eventServiceParam,
            final EventMapper eventMapperParam) {
        this.eventService = eventServiceParam;
        this.eventMapper = eventMapperParam;
    }

    /**
     * Cria um novo evento.
     *
     * @param eventDto Dados do evento a ser criado
     * @return Evento criado
     */
    @PostMapping
    @Operation(summary = "Criar evento", description = "Cria um novo evento")
    @ApiResponse(responseCode = "201", description = "Evento criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
    @CacheEvict(allEntries = true)
    public ResponseEntity<EventDto> create(
            @Valid @RequestBody final EventDto eventDto) {
        log.info("Criando novo evento: {}", eventDto.name());
        Event event = eventService.create(eventDto);
        log.info("Evento criado com ID: {}", event.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventMapper.toDto(event));
    }

    /**
     * Atualiza um evento existente.
     *
     * @param id ID do evento a ser atualizado
     * @param eventDto Novos dados do evento
     * @return Evento atualizado
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar evento", 
            description = "Atualiza um evento existente")
    @ApiResponse(responseCode = "200", 
            description = "Evento atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
    @CacheEvict(allEntries = true)
    public ResponseEntity<EventDto> update(
            @Parameter(description = "ID do evento") @PathVariable final Long id,
            @Valid @RequestBody final EventDto eventDto) {
        log.info("Atualizando evento com ID: {}", id);
        Event event = eventService.update(id, eventDto);
        log.info("Evento atualizado: {}", event.getId());
        return ResponseEntity.ok(eventMapper.toDto(event));
    }

    /**
     * Busca um evento pelo ID.
     *
     * @param id ID do evento
     * @return Evento encontrado
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar evento por ID", 
            description = "Retorna um evento pelo ID")
    @ApiResponse(responseCode = "200", description = "Evento encontrado")
    @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    @Cacheable(key = "#id")
    public ResponseEntity<EventDto> getById(
            @Parameter(description = "ID do evento") 
            @PathVariable final Long id) {
        log.info("Buscando evento com ID: {}", id);
        EventDto eventDto = eventService.getById(id);
        return ResponseEntity.ok(eventDto);
    }

    /**
     * Lista todos os eventos com paginação.
     *
     * @param pageable Informações de paginação
     * @return Lista de eventos
     */
    @GetMapping
    @Operation(summary = "Listar eventos", 
            description = "Lista todos os eventos com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de eventos")
    @Cacheable
    public ResponseEntity<Iterable<EventDto>> listEvents(final Pageable pageable) {
        log.info("Listando eventos. Page: {}, Size: {}", 
                pageable.getPageNumber(), pageable.getPageSize());
        Iterable<EventDto> eventDtos = eventService.getAll(pageable);
        return ResponseEntity.ok(eventDtos);
    }

    /**
     * Remove um evento (soft delete).
     *
     * @param id ID do evento a ser removido
     * @return Sem conteúdo
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Remover evento", 
            description = "Remove um evento (soft delete)")
    @ApiResponse(responseCode = "204", 
            description = "Evento removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
    @CacheEvict(allEntries = true)
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do evento") 
            @PathVariable final Long id) {
        log.info("Removendo evento com ID: {}", id);
        eventService.delete(id);
        log.info("Evento removido: {}", id);
        return ResponseEntity.noContent().build();
    }
}
