package kz.qasqir.qasqirinventory.api.service;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.exception.UserNotFoundException;
import kz.qasqir.qasqirinventory.api.model.dto.UserDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Role;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.model.request.UserRoleResetRequest;
import kz.qasqir.qasqirinventory.api.repository.RoleRepository;
import kz.qasqir.qasqirinventory.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository)
    {
        this.roleRepository = roleRepository;
    }

    @Transactional
    public void addForUser(Long userId, Long roleId) {
        roleRepository.addForUser(userId, roleId);
    }

    public List<Role> getAllForUserId(Long userId) {
        return roleRepository.getAllForUserId(userId);
    }

}
