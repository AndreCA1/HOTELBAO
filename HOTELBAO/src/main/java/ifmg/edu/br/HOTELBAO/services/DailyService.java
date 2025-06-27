package ifmg.edu.br.HOTELBAO.services;

import ifmg.edu.br.HOTELBAO.dtos.ClientDTO;
import ifmg.edu.br.HOTELBAO.dtos.DailyDTO;
import ifmg.edu.br.HOTELBAO.dtos.DailyInsertDTO;
import ifmg.edu.br.HOTELBAO.entities.Client;
import ifmg.edu.br.HOTELBAO.entities.Daily;
import ifmg.edu.br.HOTELBAO.entities.Role;
import ifmg.edu.br.HOTELBAO.entities.Room;
import ifmg.edu.br.HOTELBAO.projections.ClientDetailsProjection;
import ifmg.edu.br.HOTELBAO.projections.DailyDetailsProjection;
import ifmg.edu.br.HOTELBAO.repository.ClientRepository;
import ifmg.edu.br.HOTELBAO.repository.DailyRepository;
import ifmg.edu.br.HOTELBAO.repository.RoomRepository;
import ifmg.edu.br.HOTELBAO.services.exceptions.ClientException;
import ifmg.edu.br.HOTELBAO.services.exceptions.DailyException;
import ifmg.edu.br.HOTELBAO.services.exceptions.DataBaseException;
import ifmg.edu.br.HOTELBAO.services.exceptions.ResourceNotFound;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DailyService {
    @Autowired
    private DailyRepository dailyRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public Page<DailyDTO> findAll(Pageable pageable){
        Page<Daily> page = dailyRepository.findAll(pageable);

        if(page.isEmpty()) throw new DailyException("Não existem estadias lançadas no sistema!");

        return page.map(DailyDTO::new);
    }

    @Transactional(readOnly = true)
    public DailyDTO findById(Long id){
        Optional<Daily> opt = dailyRepository.findById(id);
        Daily entity = opt.orElseThrow(() -> new ResourceNotFound("Daily " + id + " not found"));
        return new DailyDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<DailyDTO> findByClientId(Long id, Pageable pageable) {
        Page<DailyDetailsProjection> result = dailyRepository.searchDailyByClientId(id, pageable);

        if (result.isEmpty()) throw new ClientException("Não existem estadias cadastradas no sistema!");

        return result.map(p -> {
            Room room = new Room();
            room.setId(p.getRoom_id());
            room.setDescription(p.getRoom_description());
            room.setPrice(p.getRoom_price());
            room.setImageUrl(p.getRoom_image_url());

            return new DailyDTO(
                    p.getId(),
                    p.getDaily_date(),
                    p.getClient_id(),
                    room
            );
        });
    }


    @Transactional
    public DailyDTO insert(DailyInsertDTO dto){

        if(dailyRepository.searchDailyByRoomAndDate(dto.getRoom(), dto.getDailyDate()) == 1) throw new DailyException("Room already reserved");
        Daily entity = new Daily();

        //Pegando cliente
        Optional<Client> obj = clientRepository.findById(dto.getClient());
        Client cliente = obj.orElseThrow(() -> new ResourceNotFound("Client not found: " + dto.getClient()));

        //Pegando quarto
        Optional<Room> obj1 = roomRepository.findById(dto.getRoom());
        Room room = obj1.orElseThrow(() -> new ResourceNotFound("Room not found: " + dto.getRoom()));

        entity.setClient(cliente);
        entity.setRoom(room);
        entity.setDailyDate(dto.getDailyDate());

        Daily novo = dailyRepository.save(entity);

        return new DailyDTO(novo);
    }

    @Transactional
    public DailyDTO update(Long id, DailyInsertDTO dto) {
        try{
            Daily entity = dailyRepository.getReferenceById(id);

            if(dailyRepository.searchDailyByRoomAndDate(dto.getRoom(), dto.getDailyDate()) == 1) throw new DailyException("Room already reserved");

            //Pegando cliente
            Optional<Client> obj = clientRepository.findById(dto.getClient());
            Client cliente = obj.orElseThrow(() -> new ResourceNotFound("Client not found: " + dto.getClient()));

            //Pegando quarto
            Optional<Room> obj1 = roomRepository.findById(dto.getRoom());
            Room room = obj1.orElseThrow(() -> new ResourceNotFound("Room not found: " + dto.getRoom()));

            entity.setClient(cliente);
            entity.setRoom(room);
            entity.setDailyDate(dto.getDailyDate());

            entity = dailyRepository.save(entity);
            return new DailyDTO(entity);

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFound("Daily not found: " + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        if(!dailyRepository.existsById(id)) {
            throw new ResourceNotFound("Daily not found: " + id);
        }
        try{
            dailyRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException("Integrity violation");
        }
    }
}
