package team07.Banking_System.controller.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team07.Banking_System.model.account.PixKeys;
import team07.Banking_System.model.transaction.PixKeyRequestDTOs;
import team07.Banking_System.services.transaction.PixKeyService;

@RestController
@RequestMapping("/pixkey")
public class PixKeyController {

    @Autowired
    private PixKeyService pixKeyService;

    @PostMapping("/email")
    public ResponseEntity<PixKeys> createEmailKey(@RequestBody PixKeyRequestDTOs.KeyValueDTO dto) {
        PixKeys registeredKey = pixKeyService.registerEmailKey(dto.getAccountId(), dto.getKeyValue());
        return ResponseEntity.ok(registeredKey);
    }

    @PostMapping("/phone")
    public ResponseEntity<PixKeys> createPhoneKey(@RequestBody PixKeyRequestDTOs.KeyValueDTO dto) {
        PixKeys registeredKey = pixKeyService.registerPhoneKey(dto.getAccountId(), dto.getKeyValue());
        return ResponseEntity.ok(registeredKey);
    }

    @PostMapping("/random")
    public ResponseEntity<PixKeys> createRandomKey(@RequestBody PixKeyRequestDTOs.KeyAccountDTO dto) {
        PixKeys registeredKey = pixKeyService.registerRandomKey(dto.getAccountId());
        return ResponseEntity.ok(registeredKey);
    }
}