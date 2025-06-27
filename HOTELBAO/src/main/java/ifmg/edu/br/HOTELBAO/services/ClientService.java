package ifmg.edu.br.HOTELBAO.services;

import ifmg.edu.br.HOTELBAO.dtos.ClientDTO;
import ifmg.edu.br.HOTELBAO.dtos.ClientInsertDTO;
import ifmg.edu.br.HOTELBAO.dtos.RoleDTO;
import ifmg.edu.br.HOTELBAO.entities.Client;
import ifmg.edu.br.HOTELBAO.entities.Role;
import ifmg.edu.br.HOTELBAO.projections.ClientDetailsProjection;
import ifmg.edu.br.HOTELBAO.repository.ClientRepository;
import ifmg.edu.br.HOTELBAO.repository.RoleRepository;
import ifmg.edu.br.HOTELBAO.services.exceptions.ClientException;
import ifmg.edu.br.HOTELBAO.services.exceptions.DataBaseException;
import ifmg.edu.br.HOTELBAO.services.exceptions.EmailException;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService implements UserDetailsService {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<ClientDTO> findAll(Pageable pageable){
        Page<Client> page = clientRepository.findAll(pageable);

        if(page.isEmpty()) throw new ClientException("Não existem clientes cadastros no sistema!");

        return page.map(ClientDTO::new);
    }

    @Transactional(readOnly = true)
    public ClientDTO findById(Long id){
        Optional<Client> opt = clientRepository.findById(id);
        Client entity = opt.orElseThrow(() -> new ResourceNotFound("Client " + id + " not found"));
        return new ClientDTO(entity);
    }

    @Transactional(readOnly = true)
    public ClientDTO findByEmail(String email){
        Client entity = clientRepository.findByEmail(email);
        return new ClientDTO(entity);
    }

    @Transactional
    public ClientDTO insert(ClientInsertDTO dto){

        //Testa se o e-mail ja esta cadastrado
        Client test = clientRepository.findByEmail(dto.getEmail());
        if(test != null) throw new EmailException("Email already registered");

        Client entity = new Client();

        copyDtoToEntity(dto, entity);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.getRoles().clear();
        entity.addRole(roleRepository.getReferenceById(2L));
        Client novo = clientRepository.save(entity);

        return new ClientDTO(novo);
    }

    @Transactional
    public ClientDTO signup(ClientInsertDTO dto) {
        Client entity = new Client();
        copyDtoToEntity(dto, entity);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.getRoles().clear();
        entity.addRole(roleRepository.getReferenceById(2L));
        entity = clientRepository.save(entity);
        return new ClientDTO(entity);
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

    @Transactional(propagation = Propagation.SUPPORTS)
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

        entity.getRoles().clear();
        for (RoleDTO roleDto : dto.getRoles()) {
            Role role = roleRepository.getReferenceById(roleDto.getId());
            entity.getRoles().add(role);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<ClientDetailsProjection> result = clientRepository.searchClientAndRoleByEmail(username);

        if (result.isEmpty()){
            throw new UsernameNotFoundException("Client not found");
        }

        Client client = new Client();
        client.setEmail(result.get(0).getClientEmail());
        client.setPassword(result.get(0).getPassword());
        for (ClientDetailsProjection p : result){
            client.addRole(new Role(p.getRoleId(), p.getAuthority()));
        }
        return client;
    }
}
