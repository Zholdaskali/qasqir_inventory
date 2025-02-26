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

    @Column(name = "document_id")
    private Long documentId;

    private String fileName;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "file_data", nullable = false)
    private byte[] fileData;


    private LocalDateTime uploadedAt = LocalDateTime.now();

    public void setFileData(byte[] fileData) {
        if (fileData == null) {
            throw new IllegalArgumentException("fileData не может быть null");
        }
        this.fileData = fileData;
    }
}

