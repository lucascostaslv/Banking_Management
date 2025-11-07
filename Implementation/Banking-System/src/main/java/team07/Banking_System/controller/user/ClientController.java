package team07.Banking_System.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team07.Banking_System.model.user.Client;
import team07.Banking_System.services.user.ClientService;

import java.util.List;

@RestController
@RequestMapping("/usr_client")
public class ClientController {
private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // LISTAR TODOS
    @GetMapping
    public ResponseEntity<List<Client>> listAll() {
        List<Client> list = clientService.listAll();
        return ResponseEntity.ok(list);
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Client> findById(@PathVariable String id) {
        return clientService.findClient(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CRIAR CLIENTE
    @PostMapping
    public ResponseEntity<Client> create(@RequestBody Client client) {
        Client created = clientService.createClient(client);
        return ResponseEntity.ok(created);
    }

    // ATUALIZAR CLIENTE
    @PutMapping("/{id}")
    public ResponseEntity<Client> update(
            @PathVariable String id,
            @RequestBody Client updatedClient
    ) {
        Client updated = clientService.updateClient(id, updatedClient);
        return ResponseEntity.ok(updated);
    }
}