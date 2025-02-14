package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrganizationDTO {
    private String bin;
    private String organizationName;
    private String email;
    private String ownerName;
    private String phoneNumber;
    private LocalDateTime registrationDate;
    private String websiteLink;
    private String address;
    private String imagePath;
}

