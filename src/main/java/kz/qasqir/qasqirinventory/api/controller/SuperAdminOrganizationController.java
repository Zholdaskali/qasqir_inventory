package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.model.entity.Organization;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/super-admin/organizations")
public class SuperAdminOrganizationController {

    private final OrganizationService organizationService;

    @Autowired
    public SuperAdminOrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping("/getAll")
    public MessageResponse<Iterable<Organization>> getAll() {
        return MessageResponse.of(organizationService.getAll());
    }

    @PostMapping("/add")
    public MessageResponse<Organization> save(@RequestBody Organization organization) {
        return MessageResponse.of(organizationService.save(organization));
    }

    @DeleteMapping("/delete/{organizationId}")
    public MessageResponse<Boolean> delete(@PathVariable Long organizationId) {
        return MessageResponse.of(organizationService.delete(organizationId));
    }



//    {
//        "userName": "SuperAdmin",
//        "password": "3-I1Xoo:ljH#?)5KkEJ"
//    }


}
