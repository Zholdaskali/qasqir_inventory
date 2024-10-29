package kz.qasqir.qasqirinventory.api.controller;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.model.entity.Organization;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/super-admin/organizations")
public class OrganizationController {
    @Autowired
    private OrganizationService organizationService;
    @GetMapping
    public MessageResponse<Iterable<Organization>> getAll() {
        return MessageResponse.of(organizationService.getAll());
    }
}
