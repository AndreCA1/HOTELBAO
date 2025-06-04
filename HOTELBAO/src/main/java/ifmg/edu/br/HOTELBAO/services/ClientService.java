package ifmg.edu.br.HOTELBAO.services;

import ifmg.edu.br.HOTELBAO.dtos.ClientDTO;
import ifmg.edu.br.HOTELBAO.dtos.ClientInsertDTO;
import ifmg.edu.br.HOTELBAO.entities.Client;
import ifmg.edu.br.HOTELBAO.repository.ClientRepository;
import ifmg.edu.br.HOTELBAO.services.exceptions.DataBaseException;
import ifmg.edu.br.HOTELBAO.services.exceptions.ResourceNotFound;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ClientService implements UserDetailsService {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<ClientDTO> findAll(Pageable page){
        Page<Client> list = clientRepository.findAll(page);

        return list.map(ClientDTO::new);
    }

    @Transactional(readOnly = true)
    public ClientDTO findById(Long id){
        Optional<Client> opt = clientRepository.findById(id);
        Client entity = opt.orElseThrow(() -> new ResourceNotFound("Client not found"));
        return new ClientDTO(entity);
    }

    //VALIDAR SE O EMAIL JA TEM CADASTRO
    @Transactional
    public ClientDTO insert(ClientInsertDTO dto){
        Client entity = new Client();

        copyDtoToEntity(dto, entity);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));

        Client novo = clientRepository.save(entity);

        return new ClientDTO(novo);
    }

    @Transactional
    public ClientDTO update(Long id, ClientDTO dto) {
        try{
            Client entity = clientRepository.getReferenceById(id);
            //joga o q está no dto para o entity
            copyDtoToEntity(dto, entity);
            entity = clientRepository.save(entity);
            return new ClientDTO(entity);

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFound("Client not found: " + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        if(!clientRepository.existsById(id)) {
            throw new ResourceNotFound("Client not found: " + id);
        }
        try{
            clientRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException("Integrity violation");
        }
    }

    private void copyDtoToEntity(ClientDTO dto, Client entity) {
        entity.setEmail(dto.getEmail());
        entity.setName(dto.getName());
        entity.setPhone(dto.getPhone());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
