package team07.Banking_System.controller.user;

import team07.Banking_System.model.user.Manager;
import team07.Banking_System.services.user.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/usr_manager")
public class ManagerController {

    private final ManagerService managerService;

    @Autowired
    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    // GET -> /usr_manager/{id}
    @GetMapping("/{id}")
    public Manager findById(@PathVariable String id) {
        return managerService.findManager(id)
                .orElseThrow(() -> new NoSuchElementException("Manager não encontrado: " + id));
    }

    // GET -> /usr_manager
    @GetMapping
    public List<Manager> listAll() {
        return managerService.listAll();
    }

    // POST -> /usr_manager
    @PostMapping
    public Manager create(@RequestBody Manager manager) {
        return managerService.createManager(manager);
    }

    // DELETE -> /usr_manager/{id}
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        managerService.deleteManager(id);
    }
}
