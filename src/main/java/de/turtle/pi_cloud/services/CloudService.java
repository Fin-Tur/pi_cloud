package de.turtle.pi_cloud.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import de.turtle.pi_cloud.models.FileEntity;
import de.turtle.pi_cloud.models.FileEntityRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
public class CloudService {

    private final String storagePath = "D:/external-hdd/pi-cloud-files"; // Beispielpfad

    @Autowired
    public FileEntityRepository fileEntityRepository;

    public FileEntity storeFile(MultipartFile file) throws IOException {
        Path dirPath = Paths.get(storagePath);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
        Path filePath = dirPath.resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), filePath);

        FileEntity entity = new FileEntity(
            file.getOriginalFilename(),
            filePath.toString(),
            file.getSize(),
            file.getContentType(),
            LocalDateTime.now()
        );
        return fileEntityRepository.save(entity);
    }

    public byte[] downloadFile(Long id) throws IOException {
        FileEntity entity = fileEntityRepository.findById(id).orElseThrow();
        Path filePath = Paths.get(entity.getPath());
        return Files.readAllBytes(filePath);
    }

    public String getCloudInfo() {
        return "Cloud information";
    }
}
