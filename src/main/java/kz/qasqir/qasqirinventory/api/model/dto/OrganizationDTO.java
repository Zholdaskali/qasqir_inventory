package kz.qasqir.qasqirinventory.api.model.dto;

import kz.qasqir.qasqirinventory.api.model.entity.Organization;

public class OrganizationDTO {
    private Long organizationId;
    private String organizationName;

    public OrganizationDTO(Long organizationId, String organizationName) {
        this.organizationId = organizationId;
        this.organizationName = organizationName;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    // Метод fromEntity, который мы определяем сами для преобразования Organization -> OrganizationDTO
    public static OrganizationDTO fromEntity(Organization organization) {
        return new OrganizationDTO(organization.getId(), organization.getName());
    }
}
