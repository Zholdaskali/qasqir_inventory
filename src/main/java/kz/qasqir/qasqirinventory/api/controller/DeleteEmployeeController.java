package kz.qasqir.qasqirinventory.api.controller;

import kz.qasqir.qasqirinventory.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/")
public class DeleteEmployeeController {
    private final UserRepository userRepository;

    @Autowired
    public DeleteEmployeeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @DeleteMapping("delete/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userRepository.deleteById(userId);
    }
}
