package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.model.entity.Organization;
import kz.qasqir.qasqirinventory.api.model.request.OrganizationRequest;
import kz.qasqir.qasqirinventory.api.model.request.RegisterRequest;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.AuthenticationService;
import kz.qasqir.qasqirinventory.api.service.OrganizationService;
import kz.qasqir.qasqirinventory.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/super-admin")
public class SuperAdminController {
    private final OrganizationService organizationService;
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @Autowired
    public SuperAdminController(OrganizationService organizationService, AuthenticationService authenticationService, UserService userService) {
        this.organizationService = organizationService;
        this.authenticationService = authenticationService;
        this.userService = userService;
    }


    @GetMapping("/organizations")
    public MessageResponse<Iterable<Organization>> getAll() {
        return MessageResponse.of(organizationService.getAll());
    }

    @PostMapping("/organization")
    public MessageResponse<Organization> save(@RequestBody OrganizationRequest organizationRequest) {
        return MessageResponse.of(organizationService.save(organizationRequest.getOrganizationName()));
    }

    @DeleteMapping("/organization/{organizationId}")
    public MessageResponse<Boolean> delete(@PathVariable Long organizationId) {
        return MessageResponse.of(organizationService.delete(organizationId));
    }

    @PostMapping("/user/sign-up")
    public MessageResponse<String> register(@RequestBody RegisterRequest registerRequest) {
        return MessageResponse.empty(authenticationService.register(registerRequest.getUserName(), registerRequest.getEmail(), registerRequest.getUserNumber(), registerRequest.getPassword(), registerRequest.getOrganizationId()));
    }

    @DeleteMapping("/user/{userId}")
    public MessageResponse<Boolean> delete(@PathVariable Long userId, Long organizationId ) {
        return MessageResponse.of(userService.deleteUserById(userId, organizationId));
    }

//        {
//            "userName": "SuperAdmin1",
//            "password": "TorgutOzalaqasqirAdminkz02"
//        }
//
//        {
//            "userName": "SuperAdmin2",
//            "password": "ErkebulanAdmin0404"
//        }
}
