package kz.qasqir.qasqirinventory.api.service.auth;

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

    @Transactional
    public void removeRoleFromUser(Long userId, Long roleId) {
        // Удаляем запись из t_user_roles, которая связывает пользователя с ролью
        roleRepository.deleteRoleFromUser(userId, roleId);
    }


    public List<Object[]> getRolesForUsers() {
        return roleRepository.findRolesForUsers();
    }
}
