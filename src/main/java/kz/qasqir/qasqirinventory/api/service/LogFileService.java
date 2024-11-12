package kz.qasqir.qasqirinventory.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class LogFileService {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Генерация текстового файла из списка объектов (универсально).
     *
     * @param logs - список объектов, который нужно записать в файл
     * @param fileNamePrefix - префикс имени файла (например, "action_logs", "exception_logs")
     * @param <T> - тип объекта в списке
     * @return ResponseEntity с файлом для скачивания
     * @throws IOException
     */
    public <T> ResponseEntity<Resource> generateLogFile(List<T> logs, String fileNamePrefix) throws IOException {
        String fileContent = generateFileContent(logs);

        Path tempFile = Files.createTempFile(fileNamePrefix, ".txt");

        try (FileWriter writer = new FileWriter(tempFile.toFile(), StandardCharsets.UTF_8)) {
            writer.write(fileContent);
        }

        Resource resource = new FileSystemResource(tempFile.toFile());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileNamePrefix + ".txt")
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }

    /**
     * Генерация содержимого файла в формате строки (например, CSV или обычный текст).
     * В этом примере мы делаем это для JSON, но можно использовать и другие форматы.
     *
     * @param logs - список объектов
     * @param <T> - тип объекта
     * @return строка, которую можно записать в файл
     * @throws IOException
     */
    private <T> String generateFileContent(List<T> logs) throws IOException {
        // Преобразуем список в JSON (или используйте CSV, если хотите)
        return objectMapper.writeValueAsString(logs);
    }
}


