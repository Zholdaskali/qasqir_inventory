package kz.qasqir.qasqirinventory.api.model.request;

public class UserRoleResetRequest {
    private Long userId;
    private Long newRoleId;
    private Long oldRoleId;
    private Long organizationId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
