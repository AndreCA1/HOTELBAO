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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/client")
@Tag(name = "User", description = "Controller/Resource for Clients")

public class ClientResource {
    @Autowired
    private ClientService clientService;

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
        return ResponseEntity.ok(entitys);
    }

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
        return ResponseEntity.ok(entity);
    }

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

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(entity.getId()).toUri();
        return ResponseEntity.created(uri).body(entity);
    }

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
    public ResponseEntity<ClientDTO> update(@Valid @PathVariable Long id, @RequestBody ClientDTO dto) {
        dto = clientService.update(id, dto);
        return ResponseEntity.ok(dto);
    }

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
