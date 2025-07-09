package ifmg.edu.br.HOTELBAO.resources;

import ifmg.edu.br.HOTELBAO.dtos.NewPasswordDTO;
import ifmg.edu.br.HOTELBAO.dtos.RequestTokenDTO;
import ifmg.edu.br.HOTELBAO.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "User", description = "Controller/Resource for users")
public class AuthResource {

    @Autowired
    private AuthService authService;

    @PostMapping(value = "recover-token")
    @Operation(
            description = "Generates a six-digit token and sends it to the customer's email",
            summary = "Create a recover token",
            responses = {
                    @ApiResponse(description = "created", responseCode = "201"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "UnAuthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403")
            })
    public ResponseEntity<Void> createRecoverToken(@Valid @RequestBody RequestTokenDTO dto){
        authService.createRecoverToken(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "reset-password")
    @Operation(
            description = "Save the new password",
            summary = "Save the new password",
            responses = {
                    @ApiResponse(description = "created", responseCode = "201"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "UnAuthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403")
            })
    public ResponseEntity<Void> resetPassword(@RequestBody NewPasswordDTO dto) {
        authService.resetPassword(dto); // valida o token e redefine a senha
        return ResponseEntity.noContent().build();
    }
}
