package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/")
public class DeleteEmployeeController {
    @Autowired
    private UserRepository userRepository;
    @DeleteMapping("delete/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userRepository.deleteById(userId);
    }
}
