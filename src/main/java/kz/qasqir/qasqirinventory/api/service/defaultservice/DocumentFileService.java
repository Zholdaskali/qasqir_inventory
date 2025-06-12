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
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentFileService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentFileService.class);

    private final DocumentFileRepository documentFileRepository;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Transactional
    public String saveDocumentFile(DocumentFileRequest request) {
        Optional<DocumentFile> existingFile = documentFileRepository.findByDocumentIdAndFileName(request.getDocumentId(), request.getFileName());
        if (existingFile.isPresent()) {
            return "Файл с таким именем уже существует для данного документа";
        }

        try {
            // Генерируем уникальный ключ (можно добавить поддиректории, если нужно)
            String uniqueKey = "documents/" + request.getDocumentId() + "/" + UUID.randomUUID() + "_" + request.getFileName();

            // Загружаем в S3
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueKey)
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(request.getFileData()));

            // Сохраняем в базу
            DocumentFile documentFile = new DocumentFile();
            documentFile.setDocumentId(request.getDocumentId());
            documentFile.setFileName(request.getFileName());
            documentFile.setFilePath(uniqueKey); // теперь это ключ в S3
            documentFile.setUploadedAt(LocalDateTime.now());

            documentFileRepository.save(documentFile);

            return "Файл успешно загружен в S3";

        } catch (Exception e) {
            logger.error("Ошибка при загрузке файла в S3", e);
            return "Ошибка при загрузке файла";
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
