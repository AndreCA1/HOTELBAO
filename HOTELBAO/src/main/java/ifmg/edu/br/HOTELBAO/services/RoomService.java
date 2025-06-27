package ifmg.edu.br.HOTELBAO.services;

import ifmg.edu.br.HOTELBAO.dtos.RoomDTO;
import ifmg.edu.br.HOTELBAO.entities.Room;
import ifmg.edu.br.HOTELBAO.repository.RoomRepository;
import ifmg.edu.br.HOTELBAO.services.exceptions.ClientException;
import ifmg.edu.br.HOTELBAO.services.exceptions.DataBaseException;
import ifmg.edu.br.HOTELBAO.services.exceptions.ResourceNotFound;
import ifmg.edu.br.HOTELBAO.services.exceptions.RoomException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<RoomDTO> findAll(Pageable pageable){
        Page<Room> page = roomRepository.findAll(pageable);

        if(page.isEmpty()) throw new RoomException("Não existem quartos cadastros no sistema!");

        return page.map(RoomDTO::new);
    }

    @Transactional(readOnly = true)
    public RoomDTO findById(Long id){
        Optional<Room> opt = roomRepository.findById(id);
        Room entity = opt.orElseThrow(() -> new ResourceNotFound("Room " + id + " not found"));
        return new RoomDTO(entity);
    }

    @Transactional
    public RoomDTO insert(RoomDTO dto){
        Room entity = new Room();

        copyDtoToEntity(dto, entity);

        Room novo = roomRepository.save(entity);

        return new RoomDTO(novo);
    }

    @Transactional
    public RoomDTO update(Long id, RoomDTO dto) {
        try{
            Room entity = roomRepository.getReferenceById(id);
            //joga o q está no dto para o entity
            copyDtoToEntity(dto, entity);
            entity = roomRepository.save(entity);
            return new RoomDTO(entity);

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFound("Room not found: " + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        if(!roomRepository.existsById(id)) {
            throw new ResourceNotFound("Room not found: " + id);
        }
        try{
            roomRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException("Integrity violation");
        }
    }

    private void copyDtoToEntity(RoomDTO dto, Room entity) {
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImageUrl(dto.getImageUrl());
    }
}
