package kz.qasqir.qasqirinventory.api.service;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.model.dto.OrganizationDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Organization;
import kz.qasqir.qasqirinventory.api.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Autowired
    public OrganizationService(OrganizationRepository organizationRepository)
    {
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public void addForAdmin(Long userId, Long organization_id) {
        organizationRepository.addForAdmin(userId, organization_id);
    }

    @Transactional
    public OrganizationDTO save(String organizationName) {
        Organization organization = createOrganization(organizationName);
        organizationRepository.save(organization);
        return new OrganizationDTO(organization.getId(),organization.getName());
    }

    @Transactional
    public Boolean delete(Long organizationId) {
        return organizationRepository.deleteOrganizationById(organizationId) > 0;
    }

    private Organization createOrganization(String organizationName) {
        return new Organization(organizationName);
    }

    public List<OrganizationDTO> getAllOrganizations() {
        return organizationRepository.findAll().stream()
                    .map(OrganizationDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
