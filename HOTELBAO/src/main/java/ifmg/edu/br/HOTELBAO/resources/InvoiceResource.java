package ifmg.edu.br.HOTELBAO.resources;

import ifmg.edu.br.HOTELBAO.services.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/invoice")
@Tag(name = "Invoice", description = "Controller/Resource for Invoices")
public class InvoiceResource {
    @Autowired
    private InvoiceService invoiceService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/client/{id}", produces = "application/json")
    @Operation(
            description = "Generate invoice for a Client",
            summary = "Generate invoice for a Client",
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "UnAuthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "NotFound", responseCode = "404")
            })
    public ResponseEntity<byte[]> findByClientId(@PathVariable Long id) {
        byte[] pdf = invoiceService.invoiceGenerate(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "nota-fiscal.pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
