package team07.Banking_System.services.transaction;

import team07.Banking_System.repository.transaction.PixRepository;
import team07.Banking_System.model.transaction.Pix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class PixService {
    private final PixRepository pixRepository;

    @Autowired
    public PixService(PixRepository pixRepository){
        this.pixRepository = pixRepository;
    }
}
