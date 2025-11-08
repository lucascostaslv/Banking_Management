package team07.Banking_System.services.user;

import team07.Banking_System.repository.user.ClientRepository;
import team07.Banking_System.model.user.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository){
        this.clientRepository = clientRepository;
    }

    public Optional<Client> findClient(String id){
        return clientRepository.findById(id);
    }

    public List<Client> listAll(){
        return clientRepository.findAll();
    }

    @Transactional
    public Client createClient(Client client){
        if (client.getCpf() == null || client.getCpf().isEmpty()) {
            throw new IllegalArgumentException("O campo CPF é obrigatório para a criação do Cliente.");
        }
        if (client.getFirst_name() == null || client.getFirst_name().isEmpty()) {
            throw new IllegalArgumentException("O primeiro nome do Cliente é obrigatório.");
        }

        client.setId(client.generateId());
        
        return clientRepository.save(client);
    }
    
    @Transactional
    public Client updateClient(String id, Client updatedClient) {
        Optional<Client> clientOptional = clientRepository.findById(id);
            
        if (clientOptional.isEmpty()) {
            throw new NoSuchElementException("Cliente com ID " + id + " não encontrado para atualização.");
        }
        
        Client existingClient = clientOptional.get();
            
        existingClient.setFirst_name(updatedClient.getFirst_name());
        existingClient.setLast_name(updatedClient.getLast_name());
        existingClient.setBirth_day(updatedClient.getBirth_day());
        
        if (!existingClient.getCpf().equals(updatedClient.getCpf())) {
             throw new IllegalArgumentException("Não é permitido alterar o CPF de um Cliente existente.");
        }
        
        return clientRepository.save(existingClient);
    }
}