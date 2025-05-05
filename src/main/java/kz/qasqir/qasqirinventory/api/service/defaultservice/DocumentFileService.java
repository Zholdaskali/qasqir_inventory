package kz.qasqir.qasqirinventory.api.service.defaultservice;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.model.dto.DocumentFileDTO;
import kz.qasqir.qasqirinventory.api.model.entity.DocumentFile;
import kz.qasqir.qasqirinventory.api.model.request.DocumentFileRequest;
import kz.qasqir.qasqirinventory.api.repository.DocumentFileRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentFileService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentFileService.class);

    private final DocumentFileRepository documentFileRepository;

    @Transactional
    public String saveDocumentFile(DocumentFileRequest request) {
        try {
            Optional<DocumentFile> existingFile = documentFileRepository.findByDocumentIdAndFileName(request.getDocumentId(), request.getFileName());
            if (existingFile.isPresent()) {
                return "Файл с таким именем уже существует для данного документа";
            }

            DocumentFile documentFile = new DocumentFile();
            documentFile.setDocumentId(request.getDocumentId());
            documentFile.setFileName(request.getFileName());
            documentFile.setFileData(request.getFileData());

            documentFileRepository.save(documentFile);
            return "Файл успешно сохранен";
        } catch (Exception e) {
            logger.error("Ошибка при сохранении файла", e);
            return "Ошибка при сохранении файла";
        }
    }

    public List<DocumentFileDTO> getDocumentFiles() {
        try {
            return documentFileRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Ошибка при получении файлов документов", e);
            throw new RuntimeException("Ошибка при получении файлов документов", e);
        }
    }

    public List<DocumentFile> getAll() {
        return documentFileRepository.findAll();
    }

    private DocumentFileDTO convertToDto(DocumentFile documentFile) {
        return new DocumentFileDTO(
                documentFile.getId(),
                documentFile.getDocumentId(),
                documentFile.getFileName(),
                documentFile.getFileData(),
                documentFile.getUploadedAt()
        );
    }

    public DocumentFile getDocumentFileById(Long id) {
        return documentFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Файл не найден"));
    }
}