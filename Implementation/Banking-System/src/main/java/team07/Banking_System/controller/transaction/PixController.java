package team07.Banking_System.controller.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import team07.Banking_System.model.transaction.Pix;
import team07.Banking_System.model.transaction.PixDTO;
import team07.Banking_System.services.transaction.PixService;

import java.net.URI;

@RestController
@RequestMapping("/pix")
public class PixController {

    @Autowired
    private PixService pixService;

    @PostMapping
    public ResponseEntity<Pix> createPix(@RequestBody PixDTO pixDTO) {
        Pix createdPix = pixService.createPixTransaction(pixDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdPix.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdPix);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pix> findById(@PathVariable String id) {
        return pixService.findPixById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}