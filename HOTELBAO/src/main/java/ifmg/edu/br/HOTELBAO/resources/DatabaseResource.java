package ifmg.edu.br.HOTELBAO.resources;

import ifmg.edu.br.HOTELBAO.dtos.MessageDTO;
import ifmg.edu.br.HOTELBAO.services.DatabaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/database")
@Tag(name = "Database", description = "Controller/Resource for database needs")
public class DatabaseResource {

    @Autowired
    private DatabaseService flyway;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/delete", produces = "application/json")
    @Operation(
            description = "Delete database",
            summary = "Use DROP to full delete database and recreate a new database after delete",
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "UnAuthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403")
            })
    public MessageDTO deleteDatabase(@RequestParam String confirmation) {
        final String requiredConfirmation = "CONFIRM_DELETE";

        if (!requiredConfirmation.equals(confirmation)) {
            return new MessageDTO("Invalid argument !", HttpStatus.FORBIDDEN.value());
        }else{
            flyway.cleanAndMigrate();
            return new MessageDTO("Database was sucessfully excluded!", HttpStatus.OK.value());
        }
    }
}
