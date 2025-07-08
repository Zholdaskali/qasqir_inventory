package kz.qasqir.qasqirinventory.api.service.media;

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
public class S3FileStorageService1 {

    private static final Logger logger = LoggerFactory.getLogger(S3FileStorageService1.class);

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
            logger.info("Начало загрузки файла в S3. DocumentId: {}, FileName: {}", request.getDocumentId(), request.getFileName());
            String uniqueKey = "documents/" + request.getDocumentId() + "/" + UUID.randomUUID() + "_" + request.getFileName();
            logger.debug("Сгенерирован уникальный ключ для файла: {}", uniqueKey);
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueKey)
                    .build();
            logger.debug("Подготовлен запрос на загрузку в бакет: {}, ключ: {}", bucketName, uniqueKey);
            s3Client.putObject(putRequest, RequestBody.fromBytes(request.getFileData()));
            logger.info("Файл успешно загружен в S3. Ключ: {}", uniqueKey);
            DocumentFile documentFile = new DocumentFile();
            documentFile.setDocumentId(request.getDocumentId());
            documentFile.setFileName(request.getFileName());
            documentFile.setFilePath(uniqueKey);
            documentFile.setUploadedAt(LocalDateTime.now());

            documentFileRepository.save(documentFile);
            logger.info("Метаданные файла сохранены в базе. ID: {}", documentFile.getId());

            return "Файл успешно загружен в S3";

        } catch (Exception e) {
            logger.error("Ошибка при загрузке файла в S3. DocumentId: {}, FileName: {}", request.getDocumentId(), request.getFileName(), e);
            return "Ошибка при загрузке файла";
        }
    }

    public List<DocumentFileDTO> getDocumentFiles() {
        try {
            logger.info("Начало получения списка файлов документов");
            List<DocumentFileDTO> files = documentFileRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            logger.info("Получено {} файлов документов", files.size());
            return files;
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
        logger.info("Поиск файла по documentId: {}", documentId);
        return documentFileRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new RuntimeException("Файл для документа не найден"));
    }
}