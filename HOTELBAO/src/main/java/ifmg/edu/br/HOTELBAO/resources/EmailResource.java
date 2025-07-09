package ifmg.edu.br.HOTELBAO.resources;

import ifmg.edu.br.HOTELBAO.dtos.EmailDTO;
import ifmg.edu.br.HOTELBAO.services.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping(value = "/email")
public class EmailResource {

    @Autowired
    private EmailService emailService;

    @PostMapping
    @Operation(
            description = "Send an email",
            summary = "Send an email",
            responses = {
                    @ApiResponse(description = "created", responseCode = "201"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "UnAuthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403")
            })
    public ResponseEntity<EntityModel<String>> sendEmail(@Valid @RequestBody EmailDTO dto){
        emailService.sendEmail(dto);

        EntityModel<String> model = EntityModel.of("Email sent successfully");
        model.add(linkTo(methodOn(EmailResource.class).sendEmail(dto)).withSelfRel());

        return ResponseEntity.ok(model);
    }
}
