package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.model.dto.UserDTO;
import kz.qasqir.qasqirinventory.api.service.Bitrix24Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/bitrix24")
public class Bitrix24Controller {
    private final Bitrix24Service bitrix24Service;

    public Bitrix24Controller(Bitrix24Service bitrix24Service) {
        this.bitrix24Service = bitrix24Service;
    }

    @GetMapping("/products")
    public ResponseEntity<List<UserDTO>> getProducts() {
        return ResponseEntity.ok(bitrix24Service.getUsers());
    }
}
