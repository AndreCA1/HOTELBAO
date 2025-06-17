package ifmg.edu.br.HOTELBAO.services;

import ifmg.edu.br.HOTELBAO.dtos.ClientDTO;
import ifmg.edu.br.HOTELBAO.dtos.DailyDTO;
import ifmg.edu.br.HOTELBAO.dtos.DailyInsertDTO;
import ifmg.edu.br.HOTELBAO.entities.Client;
import ifmg.edu.br.HOTELBAO.entities.Daily;
import ifmg.edu.br.HOTELBAO.entities.Room;
import ifmg.edu.br.HOTELBAO.repository.ClientRepository;
import ifmg.edu.br.HOTELBAO.repository.DailyRepository;
import ifmg.edu.br.HOTELBAO.repository.RoomRepository;
import ifmg.edu.br.HOTELBAO.services.exceptions.DataBaseException;
import ifmg.edu.br.HOTELBAO.services.exceptions.ResourceNotFound;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return page.map(DailyDTO::new);
    }

    @Transactional(readOnly = true)
    public DailyDTO findById(Long id){
        Optional<Daily> opt = dailyRepository.findById(id);
        Daily entity = opt.orElseThrow(() -> new ResourceNotFound("Daily " + id + " not found"));
        return new DailyDTO(entity);
    }

    @Transactional
    public DailyDTO insert(DailyInsertDTO dto){
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

    private void copyDtoToEntity(DailyDTO dto, Daily entity) {
        entity.setDailyDate(dto.getDailyDate());
        entity.setClient(dto.getClient());
        entity.setRoom(dto.getRoom());
    }
}
