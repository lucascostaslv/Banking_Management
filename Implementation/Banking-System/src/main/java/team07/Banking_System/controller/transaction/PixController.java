package team07.Banking_System.controller.transaction;

import team07.Banking_System.model.transaction.Pix;
import team07.Banking_System.services.transaction.PixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pix")
public class PixController {
    private final PixService pixService;

    @Autowired
    public PixController(PixService pixService) {
        this.pixService = pixService;
    }

    @GetMapping("/{id}")
    public Optional<Pix> findPix(@PathVariable String id) {
        return pixService.findPix(id);
    }

    @GetMapping("/list/{acc_org}")
    public List<Pix> listAll(@PathVariable Pix acc_org) {
        return pixService.listAll(acc_org);
    }

    @PostMapping
    public Pix createPix(@RequestBody Pix pix) {
        return pixService.createPix(pix);
    }
}
