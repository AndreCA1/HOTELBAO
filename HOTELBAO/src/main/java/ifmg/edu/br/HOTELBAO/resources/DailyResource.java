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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/daily")
@Tag(name = "Daily", description = "Controller/Resource for Daily")
public class DailyResource {

    @Autowired
    DailyService dailyService;

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
        return ResponseEntity.ok(entitys);
    }

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
        return ResponseEntity.ok(entity);
    }

    @GetMapping(value = "/client/{id}", produces = "application/json")
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
    public ResponseEntity<Page<DailyDTO>> findByClientId(@PathVariable Long id, Pageable pageable) {
        Page<DailyDTO> entitys = dailyService.findByClientId(id, pageable);
        return ResponseEntity.ok(entitys);
    }

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

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(entity.getId())
                .toUri();

        return ResponseEntity.created(uri).body(entity);
    }

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
    public ResponseEntity<DailyDTO> update(@Valid @PathVariable Long id, @RequestBody DailyInsertDTO dto) {
        DailyDTO responseDTO = dailyService.update(id, dto);
        return ResponseEntity.ok(responseDTO);
    }

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
