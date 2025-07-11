package ifmg.edu.br.HOTELBAO.resources;

import ifmg.edu.br.HOTELBAO.dtos.DailyDTO;
import ifmg.edu.br.HOTELBAO.dtos.DailyInsertDTO;
import ifmg.edu.br.HOTELBAO.services.DailyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/daily")
@Tag(name = "Daily", description = "Controller/Resource for Daily")
public class DailyResource {

    @Autowired
    DailyService dailyService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping(produces = "application/json")
    @Operation(
            description = "Find all Daily",
            summary = "Find all Daily",
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "UnAuthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403")
            })
    public ResponseEntity<Page<DailyDTO>> findAll(Pageable pageable) {
        Page<DailyDTO> entitys = dailyService.findAll(pageable);

        entitys.forEach(dto -> {
            dto.add(linkTo(methodOn(DailyResource.class).findById(dto.getId())).withSelfRel());
        });

        CollectionModel<DailyDTO> model = CollectionModel.of(entitys.getContent());
        model.add(linkTo(methodOn(DailyResource.class).findAll(pageable)).withSelfRel());

        return ResponseEntity.ok(entitys);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/{id}", produces = "application/json")
    @Operation(
            description = "Find daily by ID",
            summary = "Find daily by ID",
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "UnAuthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "NotFound", responseCode = "404")
            })
    public ResponseEntity<DailyDTO> findById(@PathVariable Long id) {
        DailyDTO entity = dailyService.findById(id);

        entity.add(linkTo(methodOn(DailyResource.class).findById(id)).withSelfRel());
        entity.add(linkTo(methodOn(DailyResource.class).findAll(Pageable.unpaged())).withRel("all-daily"));
        entity.add(linkTo(methodOn(DailyResource.class).update(id, null)).withRel("update"));
        entity.add(linkTo(methodOn(DailyResource.class).delete(id)).withRel("delete"));

        return ResponseEntity.ok(entity);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CLIENT')")
    @GetMapping(value = "/client/{id}", produces = "application/json")
    @Operation(
            description = "Find daily by Client",
            summary = "Find daily by Client",
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "UnAuthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "NotFound", responseCode = "404")
            })
    public ResponseEntity<Page<DailyDTO>> findByClientId(@PathVariable Long id, Pageable pageable) {
        Page<DailyDTO> entitys = dailyService.searchByClientId(id, pageable);

        entitys.forEach(dto -> {
            dto.add(linkTo(methodOn(DailyResource.class).findById(dto.getId())).withSelfRel());
        });

        CollectionModel<DailyDTO> model = CollectionModel.of(entitys.getContent());
        model.add(linkTo(methodOn(DailyResource.class).findByClientId(id, pageable)).withSelfRel());

        return ResponseEntity.ok(entitys);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CLIENT')")
    @GetMapping(value = "/client-expensive/{id}", produces = "application/json")
    @Operation(
            description = "Find daily by Client",
            summary = "Find daily by Client",
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "UnAuthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "NotFound", responseCode = "404")
            })
    public ResponseEntity<DailyDTO> findMoreExpensiveByClientId(@PathVariable Long id) {
        DailyDTO entity = dailyService.searchMoreExpensiveByClientId(id);

        entity.add(linkTo(methodOn(DailyResource.class).findById(entity.getId())).withSelfRel());

        return ResponseEntity.ok(entity);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CLIENT')")
    @GetMapping(value = "/client-cheaper/{id}", produces = "application/json")
    @Operation(
            description = "Find daily by Client",
            summary = "Find daily by Client",
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "UnAuthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "NotFound", responseCode = "404")
            })
    public ResponseEntity<DailyDTO> findCheaperByClientId(@PathVariable Long id) {
        DailyDTO entity = dailyService.searchCheaperByClientId(id);

        entity.add(linkTo(methodOn(DailyResource.class).findById(entity.getId())).withSelfRel());

        return ResponseEntity.ok(entity);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CLIENT')")
    @PostMapping(produces = "application/json")
    @Operation(
            description = "Create a new daily",
            summary = "Create a new daily",
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "UnAuthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403")
            })
    public ResponseEntity<DailyDTO> insert(@Valid @RequestBody DailyInsertDTO dto) {
        DailyDTO entity = dailyService.insert(dto);

        entity.add(linkTo(methodOn(DailyResource.class).findById(entity.getId())).withSelfRel());
        entity.add(linkTo(methodOn(DailyResource.class).update(entity.getId(), dto)).withRel("update"));
        entity.add(linkTo(methodOn(DailyResource.class).delete(entity.getId())).withRel("delete"));

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(entity.getId())
                .toUri();

        return ResponseEntity.created(uri).body(entity);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PutMapping(value = "/{id}", produces = "application/json")
    @Operation(
            description = "Update daily",
            summary = "Update daily",
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "UnAuthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "NotFound", responseCode = "404")
            })
    public ResponseEntity<DailyDTO> update(@PathVariable Long id, @Valid @RequestBody DailyInsertDTO dto) {
        DailyDTO responseDTO = dailyService.update(id, dto);

        responseDTO.add(linkTo(methodOn(DailyResource.class).findById(id)).withSelfRel());
        responseDTO.add(linkTo(methodOn(DailyResource.class).delete(id)).withRel("delete"));

        return ResponseEntity.ok(responseDTO);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    @Operation(
            description = "Delete daily",
            summary = "Delete daily",
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "UnAuthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "NotFound", responseCode = "404")
            })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        dailyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
