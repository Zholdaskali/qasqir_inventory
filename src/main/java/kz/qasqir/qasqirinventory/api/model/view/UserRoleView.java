package kz.qasqir.qasqirinventory.api.model.view;

import jakarta.persistence.*;

@Entity
@Table(name = "vw_users_roles")
public class UserRoleView {

    @Id
    private Long userId;

    private String userName;

    private Long roleId;

    private String roleName;

    // Конструкторы
    public UserRoleView() {
    }

    public UserRoleView(Long userId, String userName, Long roleId, String roleName) {
        this.userId = userId;
        this.userName = userName;
        this.roleId = roleId;
        this.roleName = roleName;
    }

    // Геттеры и сеттеры
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
