package team07.Banking_System.controller.transaction;

import team07.Banking_System.model.transaction.Pix;
import team07.Banking_System.services.transaction.PixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pix")
public class PixController {
    private final PixService pixService;

    @Autowired
    public PixController(PixService pixService) {
        this.pixService = pixService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pix> findPix(@PathVariable String id) {
        return pixService.findPix(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/list/by-account/{accountId}")
    public ResponseEntity<List<Pix>> listAllByAccount(@PathVariable String accountId) {
        List<Pix> pixTransactions = pixService.listAllByAccountId(accountId);
        return ResponseEntity.ok(pixTransactions);
    }

    @PostMapping
    public ResponseEntity<Pix> createPix(@RequestBody Pix pix) {
        Pix createdPix = pixService.createPix(pix);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdPix.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdPix);
    }
}
