package kz.qasqir.qasqirinventory.api.model.dto;

import java.time.LocalDateTime;

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

    // Default constructor


    // Constructor with fields
    public OrganizationDTO(String bin, String organizationName, String email, String ownerName,
                           String phoneNumber, LocalDateTime registrationDate, String websiteLink,
                           String address, String imagePath) {
        this.bin = bin;
        this.organizationName = organizationName;
        this.email = email;
        this.ownerName = ownerName;
        this.phoneNumber = phoneNumber;
        this.registrationDate = registrationDate;
        this.websiteLink = websiteLink;
        this.address = address;
        this.imagePath = imagePath;
    }

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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}

