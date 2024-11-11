//package kz.qasqir.qasqirinventory.api.controller;
//
//import kz.qasqir.qasqirinventory.api.model.entity.Organization;
//import kz.qasqir.qasqirinventory.api.model.request.OrganizationRequest;
//import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
//import kz.qasqir.qasqirinventory.api.service.OrganizationService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/v1/super-admin/organization")
//public class SuperAdminOrganizationController {
//
//    private final OrganizationService organizationService;
//
//    @Autowired
//    public SuperAdminOrganizationController(OrganizationService organizationService) {
//        this.organizationService = organizationService;
//    }
//
//    @GetMapping
//    public MessageResponse<Iterable<Organization>> getAll() {
//        return MessageResponse.of(organizationService.getAll());
//    }
//
//    @PostMapping
//    public MessageResponse<Organization> save(@RequestBody OrganizationRequest organizationRequest) {
//        return MessageResponse.of(organizationService.save(organizationRequest.getOrganizationName()));
//    }
//
//    @DeleteMapping("{organizationId}")
//    public MessageResponse<Boolean> delete(@PathVariable Long organizationId) {
//        return MessageResponse.of(organizationService.delete(organizationId));
//    }
//
//
//    //    {
//    //        "userName": "SuperAdmin1",
//    //        "password": "TorgutOzalaqasqirAdminkz02"
//    //    }
//
//    //    {
//    //        "userName": "SuperAdmin2",
//    //        "password": "ErkebulanAdmin0404"
//    //    }
//
//
//}