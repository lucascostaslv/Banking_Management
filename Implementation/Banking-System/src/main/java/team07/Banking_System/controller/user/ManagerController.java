package team07.Banking_System.controller.user;

import team07.Banking_System.model.user.Manager;
import team07.Banking_System.services.user.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/usr_manager")
public class ManagerController {

    private final ManagerService managerService;

    @Autowired
    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Manager> findById(@PathVariable String id) {
        return managerService.findManager(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Manager>> listAll() {
        List<Manager> managers = managerService.listAll();
        return ResponseEntity.ok(managers);
    }

    @PostMapping
    public ResponseEntity<Manager> create(@RequestBody Manager manager) {
        Manager createdManager = managerService.createManager(manager);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdManager.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdManager);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        managerService.deleteManager(id);
        return ResponseEntity.noContent().build();
    }
}
