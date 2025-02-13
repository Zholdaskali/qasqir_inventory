package kz.qasqir.qasqirinventory.api.service;

import org.springframework.stereotype.Service;

@Service
public class InventAddMailService {
    private final AsyncMailService asyncMailService;

    public InventAddMailService(AsyncMailService asyncMailService) {
        this.asyncMailService = asyncMailService;
    }


}
