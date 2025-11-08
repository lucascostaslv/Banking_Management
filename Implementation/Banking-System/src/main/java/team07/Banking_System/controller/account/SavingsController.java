package team07.Banking_System.controller.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import team07.Banking_System.model.account.Savings;
import team07.Banking_System.services.account.SavingsService;

import java.net.URI;

@RestController
@RequestMapping("/savings") // rota base para endpoints de conta poupança
public class SavingsController {

    private final SavingsService savingsService;

    @Autowired
    public SavingsController(SavingsService savingsService) {
        this.savingsService = savingsService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Savings> getSavingsById(@PathVariable String id) {
        return savingsService.findAccount(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Savings> createSavings(@RequestBody Savings savings) {
        Savings createdAccount = savingsService.createSavings(savings);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdAccount.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdAccount);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Savings> updateSavings(
            @PathVariable String id,
            @RequestBody Savings savings
    ) {

        savings.setId(id);
        Savings updated = savingsService.updateCurrent(savings);
        return ResponseEntity.ok(updated);
    }
}
