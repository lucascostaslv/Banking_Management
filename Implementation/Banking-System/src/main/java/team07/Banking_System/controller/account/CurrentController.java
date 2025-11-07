package team07.Banking_System.controller.account;

import team07.Banking_System.model.account.Current;
import team07.Banking_System.services.account.CurrentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

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
        Optional<Current> currentOpt = currentService.findAccount(id);

        return currentOpt.map(ResponseEntity::ok) .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Current> createCurrent(@RequestBody Current current) {
        Current created = currentService.createCurrent(current);
        return ResponseEntity.ok(created);
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
