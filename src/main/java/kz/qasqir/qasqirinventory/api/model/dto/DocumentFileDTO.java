package kz.qasqir.qasqirinventory.api.model.dto;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DocumentFileDTO {
    private Long id;
    private Long documentId;
    private String fileName;
    private String filePath;
    private LocalDateTime uploadedAt;
}
