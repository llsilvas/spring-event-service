package br.dev.leandro.spring.event.controller;

import br.dev.leandro.spring.event.controller.dto.OrganizerDto;
import br.dev.leandro.spring.event.entity.Organizer;
import br.dev.leandro.spring.event.mapper.OrganizerMapper;
import br.dev.leandro.spring.event.service.OrganizerService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/organizers")
public class OrganizerController {

    private final OrganizerService organizerService;
    private final OrganizerMapper organizerMapper;

    public OrganizerController(OrganizerService organizerService, OrganizerMapper organizerMapper) {
        this.organizerService = organizerService;
        this.organizerMapper = organizerMapper;
    }

    @PostMapping
    public void createOrganizer(@RequestBody OrganizerDto organizerDto) {
        Organizer organizer = organizerService.create(organizerDto);
    }

    @PutMapping
    public ResponseEntity<OrganizerDto> updateOrganizer(@PathVariable Long id, @RequestBody OrganizerDto organizerDto) {
        return ResponseEntity.ok(organizerDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizerDto> getOrganizerById(@PathVariable Long id) {
        return ResponseEntity.ok(organizerDto);
    }

    @GetMapping
    public ResponseEntity<Iterable<OrganizerDto>> listOrganizers(Pageable pageable) {
        Iterable<OrganizerDto> organizerDtos = null;
        return ResponseEntity.ok(null);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganizer(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }
}
