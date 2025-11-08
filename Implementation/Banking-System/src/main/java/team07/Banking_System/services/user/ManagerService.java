package team07.Banking_System.services.user;

import team07.Banking_System.repository.user.ManagerRepository;
import team07.Banking_System.model.user.Manager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

@Service
public class ManagerService {
    private final ManagerRepository managerRepository;

    @Autowired
    public ManagerService(ManagerRepository managerRepository){
        this.managerRepository = managerRepository;
    }

    public Optional<Manager> findManager(String id){
        return managerRepository.findById(id);
    }

    public List<Manager> listAll(){
        return managerRepository.findAll();
    }

    @Transactional
    public Manager createManager(Manager manager){
        if (manager.getCpf() == null || manager.getCpf().isEmpty()) {
            throw new IllegalArgumentException("O campo CPF é obrigatório para a criação do Cliente.");
        }
        if (manager.getFirst_name() == null || manager.getFirst_name().isEmpty()) {
            throw new IllegalArgumentException("O primeiro nome do Cliente é obrigatório.");
        }

        manager.setId(manager.generateId());

        return managerRepository.save(manager);
    }

    @Transactional
    public void deleteManager(String id){
        Optional<Manager> managerOptional = managerRepository.findById(id);

        if (managerOptional.isEmpty()) {
            throw new NoSuchElementException("Manager com ID " + id + " não encontrado para exclusão.");
        }
        
        if (managerRepository.count() <= 1) {
             throw new IllegalStateException("Não é possível deletar o último Manager do sistema.");
        }

        managerRepository.deleteById(id);
    }
}
