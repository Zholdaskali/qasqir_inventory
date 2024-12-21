package kz.qasqir.qasqirinventory.api.service;

import org.springframework.stereotype.Service;

@Service
public class InventAddMalService {
    private final AsyncMailService asyncMailService;

    public InventAddMalService(AsyncMailService asyncMailService) {
        this.asyncMailService = asyncMailService;
    }


}
