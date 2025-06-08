package kz.qasqir.qasqirinventory.api.service.defaultservice;

import jakarta.transaction.Transactional;
import kz.qasqir.qasqirinventory.api.model.dto.DocumentFileDTO;
import kz.qasqir.qasqirinventory.api.model.entity.DocumentFile;
import kz.qasqir.qasqirinventory.api.model.request.DocumentFileRequest;
import kz.qasqir.qasqirinventory.api.repository.DocumentFileRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentFileService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentFileService.class);

    private final DocumentFileRepository documentFileRepository;

    @Value("${file.directory.link}")
    private String uploadDir;

    @Transactional
    public String saveDocumentFile(DocumentFileRequest request) {
        Optional<DocumentFile> existingFile = documentFileRepository.findByDocumentIdAndFileName(request.getDocumentId(), request.getFileName());
        if (existingFile.isPresent()) {
            return "Файл с таким именем уже существует для данного документа";
        }

        try {
            Path uploadPath = Paths.get(uploadDir, String.valueOf(request.getDocumentId()));
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String uniqueFileName = UUID.randomUUID() + "_" + request.getFileName();

            Path filePath = uploadPath.resolve(uniqueFileName);

            Files.write(filePath, request.getFileData());

            DocumentFile documentFile = new DocumentFile();
            documentFile.setDocumentId(request.getDocumentId());
            documentFile.setFileName(request.getFileName());  // оригинальное имя
            documentFile.setFilePath(filePath.toString());

            documentFileRepository.save(documentFile);

            return "Файл успешно сохранен";

        } catch (IOException e) {
            logger.error("Ошибка при сохранении файла на диск", e);
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

    private DocumentFileDTO convertToDto(DocumentFile documentFile) {
        return new DocumentFileDTO(
                documentFile.getId(),
                documentFile.getDocumentId(),
                documentFile.getFileName(),
                documentFile.getFilePath(),
                documentFile.getUploadedAt()
        );
    }

    public DocumentFile getDocumentFileByDocumentId(Long documentId) {
        return documentFileRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new RuntimeException("Файл для документа не найден"));
    }
}
