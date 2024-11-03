package kz.qasqir.qasqirinventory.api.service;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.model.entity.Organization;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.model.request.OrganizationRequest;
import kz.qasqir.qasqirinventory.api.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Autowired
    public OrganizationService(OrganizationRepository organizationRepository)
    {
        this.organizationRepository = organizationRepository;
    }

    public Iterable<Organization> getAll() {
        return organizationRepository.findAll();
    }

    @Transactional
    public void addForAdmin(Long userId, Long organization_id) {
        organizationRepository.addForAdmin(userId, organization_id);
    }

    @Transactional
    public Organization save(String organizationName) {
        Organization organization = createOrganization(organizationName);
        return organizationRepository.save(organization);
    }

    @Transactional
    public Boolean delete(Long organizationId) {
        return organizationRepository.deleteOrganizationById(organizationId) > 0;
    }

    private Organization createOrganization(String organizationName) {
        return new Organization(organizationName);
    }

}
