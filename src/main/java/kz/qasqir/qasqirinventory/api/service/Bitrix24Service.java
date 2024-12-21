package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.model.dto.UserDTO;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class Bitrix24Service {

    @Value("${bitrix24.webhook-url}")
    private String WEB_HOOK_BITRIX24;

    private final RestTemplate restTemplate;
    private final UserService userService;

    public Bitrix24Service(RestTemplateBuilder restTemplateBuilder, UserService userService) {
        this.restTemplate = restTemplateBuilder.build();
        this.userService = userService;
    }

    public List<UserDTO> getUsers() {
        return userService.getAllUsers();
    }
}

