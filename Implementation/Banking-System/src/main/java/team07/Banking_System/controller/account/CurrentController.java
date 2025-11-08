package team07.Banking_System.controller.account;

import team07.Banking_System.model.account.Current;
import team07.Banking_System.services.account.CurrentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/acc_current")
public class CurrentController {
    private final CurrentService currentService;

    @Autowired
    public CurrentController(CurrentService currentService) {
        this.currentService = currentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Current> getCurrentById(@PathVariable String id) {
        return currentService.findAccount(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Current>> listAll() {
        List<Current> list = currentService.listAll();
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<Current> createCurrent(@RequestBody Current current) {
        Current createdAccount = currentService.createCurrent(current);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdAccount.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdAccount);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Current> updateCurrent(
            @PathVariable String id,
            @RequestBody Current current
    ) {
        current.setId(id);
        Current updated = currentService.updateCurrent(current);
        return ResponseEntity.ok(updated);
    }
}
