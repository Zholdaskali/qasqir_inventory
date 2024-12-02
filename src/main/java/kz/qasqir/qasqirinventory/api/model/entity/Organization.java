package kz.qasqir.qasqirinventory.api.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_organization")
public class Organization {

    @Id
    @Column(name = "bin", length = 12, nullable = false)
    private String bin; // Unique Business Identification Number (BIN)

    @Column(name = "organization_name", length = 255, nullable = false)
    private String organizationName; // Organization name

    @Column(name = "email", length = 254, nullable = false, unique = true)
    private String email; // Email

    @Column(name = "owner_name", length = 255, nullable = false)
    private String ownerName; // Owner name

    @Column(name = "phone_number", length = 15, nullable = false, unique = true)
    private String phoneNumber; // Phone number

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate; // Registration date

    @Column(name = "website_link", length = 255, nullable = false)
    private String websiteLink; // Website link

    @Column(name = "address", nullable = false)
    private String address; // Full address

    @Column(name = "image_id")
    private Long imageId;

    // Getters and Setters

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getWebsiteLink() {
        return websiteLink;
    }

    public void setWebsiteLink(String websiteLink) {
        this.websiteLink = websiteLink;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }
}

