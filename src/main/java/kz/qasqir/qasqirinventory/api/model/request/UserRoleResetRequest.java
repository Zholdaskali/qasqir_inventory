package kz.qasqir.qasqirinventory.api.model.request;

import java.util.List;

public class UserRoleResetRequest {
    private List<Long> newRoles;

    public List<Long> getNewRoles() {
        return newRoles;
    }

    public void setNewRoles(List<Long> newRoles) {
        this.newRoles = newRoles;
    }
}
