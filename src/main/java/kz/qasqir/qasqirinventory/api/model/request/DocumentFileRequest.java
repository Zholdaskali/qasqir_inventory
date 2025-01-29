package kz.qasqir.qasqirinventory.api.model.request;

import jakarta.persistence.Lob;
import lombok.Data;

@Data
public class DocumentFileRequest {
    private Long documentId;
    private String fileName;
    private byte[] fileData;
}
