package kz.qasqir.qasqirinventory.api.model.request;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentFileRequest {
    private Long documentId;
    private String fileName;
    private byte[] fileData;
}
