package ifmg.edu.br.HOTELBAO.resources;

import ifmg.edu.br.HOTELBAO.dtos.ClientDTO;
import ifmg.edu.br.HOTELBAO.dtos.ClientInsertDTO;
import ifmg.edu.br.HOTELBAO.services.ClientService;
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
@RequestMapping("/client")
@Tag(name = "User", description = "Controller/Resource for Clients")

public class ClientResource {
    @Autowired
    private ClientService clientService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping(produces = "application/json")
    @Operation(
            description = "Find all Clients",
            summary = "Find all Clients",
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "UnAuthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403")
            })
    public ResponseEntity<Page<ClientDTO>> findAll(Pageable pageable) {
        Page<ClientDTO> entitys = clientService.findAll(pageable);

        entitys.forEach(dto -> {
            dto.add(linkTo(methodOn(ClientResource.class).findById(dto.getId())).withSelfRel());
        });

        CollectionModel<ClientDTO> model = CollectionModel.of(entitys.getContent());
        model.add(linkTo(methodOn(ClientResource.class).findAll(pageable)).withSelfRel());

        return ResponseEntity.ok(entitys);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/{id}", produces = "application/json")
    @Operation(
            description = "Find client by ID",
            summary = "Find client by ID",
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "UnAuthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "NotFound", responseCode = "404")
            })
    public ResponseEntity<ClientDTO> findById(@PathVariable Long id) {
        ClientDTO entity = clientService.findById(id);

        entity.add(linkTo(methodOn(ClientResource.class).findById(id)).withSelfRel());
        entity.add(linkTo(methodOn(ClientResource.class).findAll(Pageable.unpaged())).withRel("all-clients"));
        entity.add(linkTo(methodOn(ClientResource.class).update(id, entity)).withRel("update"));
        entity.add(linkTo(methodOn(ClientResource.class).delete(id)).withRel("delete"));

        return ResponseEntity.ok(entity);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT')")
    @GetMapping(value = "/email/{email}", produces = "application/json")
    @Operation(
            description = "Find client by EMAIL",
            summary = "Find client by EMAIL",
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "UnAuthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "NotFound", responseCode = "404")
            })
    public ResponseEntity<ClientDTO> findByEmail(@PathVariable String email) {
        ClientDTO entity = clientService.findByEmail(email);

        entity.add(linkTo(methodOn(ClientResource.class).findById(entity.getId())).withSelfRel());
        entity.add(linkTo(methodOn(ClientResource.class).update(entity.getId(), entity)).withRel("update"));
        entity.add(linkTo(methodOn(ClientResource.class).delete(entity.getId())).withRel("delete"));

        return ResponseEntity.ok(entity);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PostMapping(produces = "application/json")
    @Operation(
            description = "Create a new client",
            summary = "Create a new client",
            responses = {
                    @ApiResponse(description = "created", responseCode = "201"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "UnAuthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403")
            })
    public ResponseEntity<ClientDTO> insert(@Valid @RequestBody ClientInsertDTO dto) {
        ClientDTO entity = clientService.insert(dto);

        entity.add(linkTo(methodOn(ClientResource.class).findById(entity.getId())).withSelfRel());
        entity.add(linkTo(methodOn(ClientResource.class).update(entity.getId(), entity)).withRel("update"));
        entity.add(linkTo(methodOn(ClientResource.class).delete(entity.getId())).withRel("delete"));

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(entity.getId()).toUri();
        return ResponseEntity.created(uri).body(entity);
    }

    @PostMapping("/signup")
    @Operation(
            description = "Customer self-registration",
            summary = "Create a new client",
            responses = {
                    @ApiResponse(description = "created", responseCode = "201"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "UnAuthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403")
            })
    public ResponseEntity<ClientDTO> signup(@RequestBody @Valid ClientInsertDTO dto) {
        ClientDTO entity = clientService.signup(dto);

        entity.add(linkTo(methodOn(ClientResource.class).findById(entity.getId())).withSelfRel());
        entity.add(linkTo(methodOn(ClientResource.class).update(entity.getId(), entity)).withRel("update"));
        entity.add(linkTo(methodOn(ClientResource.class).delete(entity.getId())).withRel("delete"));

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(entity.getId()).toUri();
        return ResponseEntity.created(uri).body(entity);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CLIENT')")
    @PutMapping(value = "/{id}", produces = "application/json")
    @Operation(
            description = "Update client",
            summary = "Update client",
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "UnAuthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "NotFound", responseCode = "404")
            })

    public ResponseEntity<ClientDTO> update(@PathVariable Long id, @Valid @RequestBody ClientDTO dto) {
        dto = clientService.update(id, dto);

        dto.add(linkTo(methodOn(ClientResource.class).findById(dto.getId())).withSelfRel());
        dto.add(linkTo(methodOn(ClientResource.class).update(dto.getId(), dto)).withRel("update"));
        dto.add(linkTo(methodOn(ClientResource.class).delete(dto.getId())).withRel("delete"));

        return ResponseEntity.ok(dto);
    }
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CLIENT')")
    @DeleteMapping(value = "/{id}")
    @Operation(
            description = "Delete client",
            summary = "Delete client",
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "UnAuthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "NotFound", responseCode = "404")
            })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
