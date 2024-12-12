package kz.qasqir.qasqirinventory.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDTO {
    private Long userId;
    private String userName;
    private String email;
    private String userNumber;
    private LocalDateTime registrationDate;
    private String imagePath;
    private boolean emailVerified;
    private List<String> userRoles; // Множественные роли

    public UserDTO(Long userId, String userName, String email, String userNumber, boolean emailVerified, List<String> userRoles, LocalDateTime registrationDate, String imagePath) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.userNumber = userNumber;
        this.emailVerified = emailVerified;
        this.userRoles = userRoles;
        this.registrationDate = registrationDate;
        this.imagePath = imagePath;
    }
}
