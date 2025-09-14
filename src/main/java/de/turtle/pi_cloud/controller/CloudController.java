package de.turtle.pi_cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import de.turtle.pi_cloud.services.CloudService;
import de.turtle.pi_cloud.models.FileEntity;
import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class CloudController {
    @Autowired
    private CloudService cloudService;

    @PostMapping("/upload")
    public ResponseEntity<FileEntity> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        FileEntity saved = cloudService.storeFile(file);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) throws IOException {
        FileEntity entity = cloudService.fileEntityRepository.findById(id).orElseThrow();
        byte[] data = cloudService.downloadFile(id);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + entity.getName())
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(data);
    }
}
