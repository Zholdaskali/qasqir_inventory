package kz.qasqir.qasqirinventory.api.service;

import kz.qasqir.qasqirinventory.api.exception.FailedToAddImageException;
import kz.qasqir.qasqirinventory.api.exception.UserNotFoundException;
import kz.qasqir.qasqirinventory.api.model.entity.Image;
import kz.qasqir.qasqirinventory.api.model.entity.User;
import kz.qasqir.qasqirinventory.api.model.response.MessageResponse;
import kz.qasqir.qasqirinventory.api.repository.ImageRepository;
import kz.qasqir.qasqirinventory.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    public ImageService(ImageRepository imageRepository, UserRepository userRepository) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
    }

    public MessageResponse<String> addImage(Long userId, MultipartFile file) {
        try {
            String filePath = saveImageToDisk(file);
            Image image = new Image();
            image.setImagePath(filePath);
            Image imageSaved = imageRepository.save(image);

            System.out.println(imageSaved.getId());
            User user = userRepository.findById(userId)
                    .orElseThrow(UserNotFoundException::new);
            user.setImageId(imageSaved.getId());
            userRepository.save(user);;
            return MessageResponse.empty("Изображение сохранено");
        } catch (Exception e) {
            System.err.println("Error while adding avatar: " + e.getMessage());
            e.printStackTrace();
            throw new FailedToAddImageException();
        }
    }

    private String saveImageToDisk(MultipartFile file) {
        String uploadDir = "E:/AAAAWORK/Work/imageQasqir/";
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        try {
            if (!Files.exists(Paths.get(uploadDir))) {
                Files.createDirectories(Paths.get(uploadDir));
            }

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.err.println("Error while saving file to disk: " + e.getMessage());
            e.printStackTrace();

            throw new FailedToAddImageException();
        }

        return filePath.toString().replace("\\", "/");
    }

    public String getImagePath(Long imageId) {
        Image image =  imageRepository.findById(imageId).orElseThrow();
        return image.getImagePath();
    }

}
