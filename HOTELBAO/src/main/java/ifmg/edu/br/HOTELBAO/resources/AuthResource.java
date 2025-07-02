package ifmg.edu.br.HOTELBAO.resources;

import ifmg.edu.br.HOTELBAO.dtos.NewPasswordDTO;
import ifmg.edu.br.HOTELBAO.dtos.ResquestTokenDTO;
import ifmg.edu.br.HOTELBAO.services.AuthService;
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
    public ResponseEntity<Void> createRecoverToken(@Valid @RequestBody ResquestTokenDTO dto){
        authService.createRecoverToken(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody NewPasswordDTO dto) {
        authService.resetPassword(dto); // valida o token e redefine a senha
        return ResponseEntity.noContent().build();
    }
}
