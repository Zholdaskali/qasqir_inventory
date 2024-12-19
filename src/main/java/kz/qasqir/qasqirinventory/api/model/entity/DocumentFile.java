package kz.qasqir.qasqirinventory.api.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_documents_files")
@Getter
@Setter
public class DocumentFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long documentId; // ID связанного документа

    private String fileName;

    @Lob
    private byte[] fileData; // Бинарные данные

    private LocalDateTime uploadedAt = LocalDateTime.now();
}

