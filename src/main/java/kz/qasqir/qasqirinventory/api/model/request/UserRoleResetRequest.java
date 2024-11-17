package kz.qasqir.qasqirinventory.api.model.request;

public class UserRoleResetRequest {
    private Long newRoleId;
    private Long oldRoleId;

    public Long getNewRoleId() {
        return newRoleId;
    }

    public void setNewRoleId(Long newRoleId) {
        this.newRoleId = newRoleId;
    }

    public Long getOldRoleId() {
        return oldRoleId;
    }

    public void setOldRoleId(Long oldRoleId) {
        this.oldRoleId = oldRoleId;
    }
}
