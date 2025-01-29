package kz.qasqir.qasqirinventory.api.service;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.model.entity.DocumentFile;
import kz.qasqir.qasqirinventory.api.model.request.DocumentFileRequest;
import kz.qasqir.qasqirinventory.api.repository.DocumentFileRepository;
import org.springframework.stereotype.Service;

@Service
public class DocumentFileService {

    private final DocumentFileRepository documentFileRepository;

    public DocumentFileService(DocumentFileRepository documentFileRepository) {
        this.documentFileRepository = documentFileRepository;
    }

    @Transactional
    public String saveDocumentFile(DocumentFileRequest request) {
        try {
            DocumentFile documentFile = new DocumentFile();
            documentFile.setDocumentId(request.getDocumentId());
            documentFile.setFileName(request.getFileName());
            documentFile.setFileData(request.getFileData());
            documentFileRepository.saveDocumentFile(request.getDocumentId(), request.getFileName(), request.getFileData());
            return "Файл успешно сохранен";
        } catch (RuntimeException e) {
            throw new RuntimeException("Ошибка при сохранении файла1");
        }
    }

//    @Transactional
//    public
}
