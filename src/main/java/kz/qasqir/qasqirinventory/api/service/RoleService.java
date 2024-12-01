package kz.qasqir.qasqirinventory.api.service;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.model.entity.Role;
import kz.qasqir.qasqirinventory.api.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
