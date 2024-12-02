package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.model.dto.OrganizationDTO;
import kz.qasqir.qasqirinventory.api.model.entity.Organization;
import kz.qasqir.qasqirinventory.api.repository.OrganizationRepository;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final ImageService imageService;

    public OrganizationService(OrganizationRepository organizationRepository, ImageService imageService) {
        this.organizationRepository = organizationRepository;
        this.imageService = imageService;
    }

    public OrganizationDTO getOrganization() {
        Organization organization = organizationRepository.findById(170640007696L).orElseThrow();
        return convertToDTO(organization);
    }

    private OrganizationDTO convertToDTO(Organization organization) {
        String imagePath = organization.getImageId() != null
                ? imageService.getImagePath(organization.getImageId())
                : null;

        return new OrganizationDTO(organization.getBin(), organization.getOrganizationName(), organization.getEmail(),
                organization.getOwnerName(), organization.getPhoneNumber(), organization.getRegistrationDate(),
                organization.getWebsiteLink(), organization.getAddress(), imagePath);
//
//        OrganizationDTO(String bin, String organizationName, String email, String ownerName,
//                String phoneNumber, LocalDateTime registrationDate, String websiteLink,
//                String address, Long imagePath)
    }
}
