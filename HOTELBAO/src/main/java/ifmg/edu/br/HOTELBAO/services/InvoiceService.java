package ifmg.edu.br.HOTELBAO.services;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import ifmg.edu.br.HOTELBAO.dtos.ClientDTO;
import ifmg.edu.br.HOTELBAO.dtos.DailyDTO;
import ifmg.edu.br.HOTELBAO.services.exceptions.ClientException;
import ifmg.edu.br.HOTELBAO.services.exceptions.DailyException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class InvoiceService {
    @Autowired
    private ClientService clientService;

    @Autowired
    private DailyService dailyService;

    @Transactional
    public byte[] invoiceGenerate(Long clientId) {
        ClientDTO client = clientService.findById(clientId);
        if(client.getName() == null || client.getEmail() == null || client.getPhone() == null) throw new ClientException("É obrigatório fornecer todos os dados do cliente!");

        Page<DailyDTO> daily = dailyService.searchByClientId(clientId, Pageable.unpaged());

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document doc = new Document();
            PdfWriter.getInstance(doc, outputStream);
            doc.open();

            Font bold = FontFactory.getFont(FontFactory.COURIER_BOLD, 12);
            Font normal = FontFactory.getFont(FontFactory.COURIER, 12);

            doc.add(new Paragraph("==========================", bold));
            doc.add(new Paragraph("NOTA FISCAL", bold));
            doc.add(new Paragraph("==========================", bold));
            doc.add(new Paragraph("Nome: " + client.getName(), normal));
            doc.add(new Paragraph("Email: " + client.getEmail(), normal));
            doc.add(new Paragraph("Telefone: " + client.getPhone(), normal));
            doc.add(new Paragraph("Endereço: Rua do centro", normal));
            doc.add(new Paragraph("Cidade: Formiga", normal));
            doc.add(new Paragraph("==========================", bold));
            doc.add(new Paragraph("===ESTADIAS===", bold));
            doc.add(new Paragraph("==========================", bold));

            double total = 0.0;
            int count = 0;

            for (DailyDTO e : daily) {
                if(e.getRoom().getDescription().isBlank() || e.getRoom().getPrice() == 0.0) continue;
                count++;
                String linha = String.format("Quarto: %-25s Valor: %.2f", e.getRoom().getDescription(), e.getRoom().getPrice());
                doc.add(new Paragraph(linha, normal));
                total += e.getRoom().getPrice();
            }

            if(count == 0) throw new DailyException("O cliente deve ter ao menos uma estadia com valor e descrição!");

            doc.add(new Paragraph("==========================", bold));
            doc.add(new Paragraph(String.format("Total: R$ %.2f", total), normal));
            doc.add(new Paragraph("==========================", bold));

            doc.close();
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF da nota fiscal", e);
        }
    }
}
