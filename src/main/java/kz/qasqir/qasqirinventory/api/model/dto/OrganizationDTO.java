package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrganizationDTO {

    private String bin; // Unique Business Identification Number (BIN)
    private String organizationName; // Organization name
    private String email; // Email
    private String ownerName; // Owner name
    private String phoneNumber; // Phone number
    private LocalDateTime registrationDate; // Registration date
    private String websiteLink; // Website link
    private String address; // Full address
    private String imagePath; // Image ID (optional)

}

