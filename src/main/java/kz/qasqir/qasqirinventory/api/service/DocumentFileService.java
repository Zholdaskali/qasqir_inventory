package kz.qasqir.qasqirinventory.api.service;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.model.dto.DocumentFileDTO;
import kz.qasqir.qasqirinventory.api.model.entity.DocumentFile;
import kz.qasqir.qasqirinventory.api.model.request.DocumentFileRequest;
import kz.qasqir.qasqirinventory.api.repository.DocumentFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentFileService {

    private final DocumentFileRepository documentFileRepository;


    @Transactional
    public String saveDocumentFile(DocumentFileRequest request) {
            DocumentFile documentFile = new DocumentFile();
            documentFile.setDocumentId(request.getDocumentId());
            documentFile.setFileName(request.getFileName());
            documentFile.setFileData(request.getFileData());
            if (documentFileRepository.saveDocumentFile(request.getDocumentId(), request.getFileName(), request.getFileData()) > 0){
                return "Файл успешно сохранен";
            }else {
                return "Ошибка при сохранении файла1";
            }
    }

    public List<DocumentFileDTO> getDocumentFile() {
        try {
            List<DocumentFile> files = documentFileRepository.findAllFiles();
            return files.stream()
                    .map(this::convertToDto)
                    .toList();
        } catch (Exception e) {
            System.out.println("Ошибка при получении файлов документов" + e);
            throw e;
        }
    }

    public List<DocumentFile> getAll() {
        return documentFileRepository.findAllFiles();
    }

    private DocumentFileDTO convertToDto(DocumentFile documentFile) {
        byte[] fileData = Base64.getDecoder().decode(documentFile.getFileData());
        return new DocumentFileDTO(
                documentFile.getId(),
                documentFile.getDocumentId(),
                documentFile.getFileName(),
                fileData,
                documentFile.getUploadedAt()
        );
    }
}
