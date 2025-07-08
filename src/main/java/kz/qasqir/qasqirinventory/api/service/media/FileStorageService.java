package kz.qasqir.qasqirinventory.api.service.media;

import io.openapitools.jackson.dataformat.hal.annotation.Resource;
import kz.qasqir.qasqirinventory.api.model.entity.DocumentFile;
import kz.qasqir.qasqirinventory.api.model.request.DocumentFileRequest;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String upload(DocumentFileRequest request);       // загружает файл и возвращает путь или ссылку
    void delete(Long id);                // удаляет файл
    Resource download(String path);          // отдает файл (если нужно для API)
    String getUrl(String path);              // возвращает ссылку на файл (если, например, фронт сам будет его забирать)
    DocumentFile getDocumentFileByDocumentId(Long documentId);
}
