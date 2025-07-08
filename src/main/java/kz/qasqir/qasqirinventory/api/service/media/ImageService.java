package kz.qasqir.qasqirinventory.api.service.media;

import kz.qasqir.qasqirinventory.api.exception.FailedToAddImageException;
import kz.qasqir.qasqirinventory.api.exception.UserNotFoundException;
import kz.qasqir.qasqirinventory.api.model.entity.Image;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.repository.ImageRepository;
import kz.qasqir.qasqirinventory.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    @Value("${image.directory.link}")
    private String UPLOAD_DIR;

    public ImageService(ImageRepository imageRepository, UserRepository userRepository) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
    }

    public MessageResponse<String> addImage(Long userId, MultipartFile file) {
        try {
            // Пытаемся найти пользователя по userId
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + userId + " не найден"));

            // Проверяем, есть ли уже изображение у пользователя
//            if (user.getImageId() != null) {
//                imageRepository.deleteById(user.getImageId()); // Можно добавить удаление старого изображения, если нужно
//            }

            // Сохраняем изображение на диск и в БД
            String imagePath = getImagePath(user.getImageId());
            if (imagePath != null) {
                deleteImageFromDisk(imagePath);
            }
            String filePath = saveImageToDisk(file);
            Image image = new Image();
            image.setImagePath(filePath);
            Image imageSaved = imageRepository.save(image);

            logger.info("Изображение успешно сохранено с id: {}", imageSaved.getId());

            user.setImageId(imageSaved.getId());
            userRepository.save(user);

            return MessageResponse.empty("Изображение сохранено");

        } catch (UserNotFoundException e) {
            logger.error("Ошибка при добавлении изображения для пользователя с id {}: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Ошибка при сохранении изображения для пользователя с id {}: {}", userId, e.getMessage(), e);
            throw new FailedToAddImageException("Не удалось добавить изображение. Попробуйте позже.");
        }
    }

    private String saveImageToDisk(MultipartFile file) {
        String uploadDir = UPLOAD_DIR;
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        try {
            // Создаем директорию, если она не существует
            Path directoryPath = Paths.get(uploadDir);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            // Копируем файл в целевую директорию
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return filePath.toString().replace("\\", "/");

        } catch (Exception e) {
            logger.error("Ошибка при сохранении изображения на диск: {}", e.getMessage(), e);
            throw new FailedToAddImageException("Не удалось сохранить изображение на диск.");
        }
    }

    private void deleteImageFromDisk(String imagePath) {
        try {
            Path path = Paths.get(imagePath);
            if (Files.exists(path)) {
                Files.delete(path);
                logger.info("Файл удалён: {}", imagePath);
            } else {
                logger.warn("Файл не найден для удаления: {}", imagePath);
            }
        } catch (IOException e) {
            logger.error("Ошибка при удалении изображения с диска: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось удалить изображение с диска.");
        }
    }


    public String getImagePath(Long imageId) {
        try {
            Image image = imageRepository.findById(imageId).orElseThrow(() -> new FailedToAddImageException("Изображение не найдено"));
            return image.getImagePath();
        } catch (Exception e) {
            logger.error("Ошибка при получении пути изображения с id {}: {}", imageId, e.getMessage(), e);
            throw new FailedToAddImageException("Не удалось получить путь изображения.");
        }
    }
}
