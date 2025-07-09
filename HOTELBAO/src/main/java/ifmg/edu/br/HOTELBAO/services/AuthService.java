package ifmg.edu.br.HOTELBAO.services;

import ifmg.edu.br.HOTELBAO.dtos.EmailDTO;
import ifmg.edu.br.HOTELBAO.dtos.NewPasswordDTO;
import ifmg.edu.br.HOTELBAO.dtos.RequestTokenDTO;
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
import java.util.Random;

@Service
public class AuthService {

    @Value("${email.password-recover.token.minutes}")
    private int tokenMinutes;


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
    public PasswordRecover saveRecoverToken(String email) {
        //gera token
        String token = String.format("%06d", new Random().nextInt(1_000_000));

        //inserir no BD
        PasswordRecover passwordRecover = new PasswordRecover();
        passwordRecover.setToken(token);
        passwordRecover.setEmail(email);
        passwordRecover.setExpiration(Instant.now().plusSeconds(tokenMinutes * 60L));

        passwordRecoverRepository.save(passwordRecover);
        return passwordRecover;
    }

    @Transactional
    public void createRecoverToken(RequestTokenDTO dto) {
        //Busca user pelo email
        Client entity = clientRepository.findByEmail(dto.getEmail());
        if (entity == null) throw new ResourceNotFound("Email not found!");

        PasswordRecover token = saveRecoverToken(dto.getEmail());

        String body = "Use o código abaixo para redefinir sua senha:" +
                "\n\nCódigo: " + token.getToken() +
                "\nVálido por " + tokenMinutes + " minutos.";
        EmailDTO email = new EmailDTO(entity.getEmail(), "Password Recover", body);
        emailService.sendEmail(email);
    }

    @Transactional
    public void resetPassword(NewPasswordDTO dto) {
        PasswordRecover recover = passwordRecoverRepository.searchValidToken(dto.getToken(), Instant.now());

        if(recover == null){
            throw new ResourceNotFound("Token not found");
        }

        Client entity = clientRepository.findByEmail(recover.getEmail());

        entity.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    }



}