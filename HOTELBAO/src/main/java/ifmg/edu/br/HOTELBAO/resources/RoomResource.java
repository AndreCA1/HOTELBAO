package ifmg.edu.br.HOTELBAO.resources;

import ifmg.edu.br.HOTELBAO.dtos.RoomDTO;
import ifmg.edu.br.HOTELBAO.services.RoomService;
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
@RequestMapping("/room")
@Tag(name = "Room", description = "Controller/Resource for Rooms")
public class RoomResource {
    @Autowired
    private RoomService roomService;

    @GetMapping(produces = "application/json")
    @Operation(
            description = "Find all Rooms",
            summary = "Find all Rooms",
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "UnAuthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403")
            })
    public ResponseEntity<Page<RoomDTO>> findAll(Pageable pageable) {
        Page<RoomDTO> entitys = roomService.findAll(pageable);

        entitys.forEach(dto -> {
            dto.add(linkTo(methodOn(RoomResource.class).findById(dto.getId())).withSelfRel());
        });

        CollectionModel<RoomDTO> model = CollectionModel.of(entitys.getContent());
        model.add(linkTo(methodOn(RoomResource.class).findAll(pageable)).withSelfRel());

        return ResponseEntity.ok(entitys);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @Operation(
            description = "Find room by ID",
            summary = "Find room by ID",
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "UnAuthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "NotFound", responseCode = "404")
            })
    public ResponseEntity<RoomDTO> findById(@PathVariable Long id) {
        RoomDTO entity = roomService.findById(id);

        entity.add(linkTo(methodOn(RoomResource.class).findById(id)).withSelfRel());
        entity.add(linkTo(methodOn(RoomResource.class).findAll(Pageable.unpaged())).withRel("all-rooms"));
        entity.add(linkTo(methodOn(RoomResource.class).update(id, entity)).withRel("update"));
        entity.add(linkTo(methodOn(RoomResource.class).delete(id)).withRel("delete"));

        return ResponseEntity.ok(entity);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PostMapping(produces = "application/json")
    @Operation(
            description = "Create a new room",
            summary = "Create a new room",
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "UnAuthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403")
            })
    public ResponseEntity<RoomDTO> insert(@Valid @RequestBody RoomDTO dto) {
        RoomDTO entity = roomService.insert(dto);

        entity.add(linkTo(methodOn(RoomResource.class).findById(entity.getId())).withSelfRel());
        entity.add(linkTo(methodOn(RoomResource.class).update(entity.getId(), entity)).withRel("update"));
        entity.add(linkTo(methodOn(RoomResource.class).delete(entity.getId())).withRel("delete"));

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(entity.getId())
                .toUri();

        return ResponseEntity.created(uri).body(entity);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PutMapping(value = "/{id}", produces = "application/json")
    @Operation(
            description = "Update room",
            summary = "Update room",
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "UnAuthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "NotFound", responseCode = "404")
            })
    public ResponseEntity<RoomDTO> update(@PathVariable Long id, @Valid @RequestBody RoomDTO dto) {
        dto = roomService.update(id, dto);

        dto.add(linkTo(methodOn(RoomResource.class).findById(dto.getId())).withSelfRel());
        dto.add(linkTo(methodOn(RoomResource.class).update(dto.getId(), dto)).withRel("update"));
        dto.add(linkTo(methodOn(RoomResource.class).delete(dto.getId())).withRel("delete"));

        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    @Operation(
            description = "Delete room",
            summary = "Delete room",
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "UnAuthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "NotFound", responseCode = "404")
            })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
