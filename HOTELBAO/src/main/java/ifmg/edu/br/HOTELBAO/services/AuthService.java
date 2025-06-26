package ifmg.edu.br.HOTELBAO.services;

import ifmg.edu.br.HOTELBAO.dtos.EmailDTO;
import ifmg.edu.br.HOTELBAO.dtos.NewPasswordDTO;
import ifmg.edu.br.HOTELBAO.dtos.ResquestTokenDTO;
import ifmg.edu.br.HOTELBAO.entities.Client;
import ifmg.edu.br.HOTELBAO.entities.PasswordRecover;
import ifmg.edu.br.HOTELBAO.repository.ClientRepository;
import ifmg.edu.br.HOTELBAO.repository.PasswordRecoverRepository;
import ifmg.edu.br.HOTELBAO.services.exceptions.ResourceNotFound;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    @Value("${email.password-recover.token.minutes}")
    private int tokenMinutes;

    @Value("${email.password-recover.uri}")
    private String uri;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientService clientService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordRecoverRepository passwordRecoverRepository;

    @Transactional
    public void createRecoverToken(ResquestTokenDTO dto) {
        //Busca user pelo email
        Client entity = clientRepository.findByEmail(dto.getEmail());

        if (entity == null) throw new ResourceNotFound("Email not found!");
        //gera token
        String token = UUID.randomUUID().toString();

        //inserir no BD
        PasswordRecover passwordRecover = new PasswordRecover();
        passwordRecover.setToken(token);
        passwordRecover.setEmail(entity.getEmail());
        passwordRecover.setExpiration(Instant.now().plusSeconds(tokenMinutes * 60L));

        passwordRecoverRepository.save(passwordRecover);

        String body = "Acesse o link para definir uma nova senha: " +
                "\nVálido por " + tokenMinutes + " minutos" +
                "\n\n" + uri + token;
        EmailDTO email = new EmailDTO(entity.getEmail(), "Password Recover", body);
        emailService.sendEmail(email);
    }

    @Transactional
    public void saveNewPassword(NewPasswordDTO dto) {
        PasswordRecover recover = passwordRecoverRepository.searchValidToken(dto.getToken(), Instant.now());

        if(recover == null){
            throw new ResourceNotFound("Token not found");
        }

        Client entity = clientRepository.findByEmail(recover.getEmail());

        entity.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    }
}