package kz.qasqir.qasqirinventory.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class GetTestController {
    @GetMapping("/admin/get")
    public String getAdmin() {
        return "getAdmin";
    }

    @GetMapping("/employee/get")
    public String getEmployee() {
        return "getEmployee";
    }

    @GetMapping("get-super-admin/get")
    public String getSuperAdmin() {
        return "getSuperAdmin";
    }
}
