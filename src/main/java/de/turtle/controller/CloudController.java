package de.turtle.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import de.turtle.models.FileEntity;
import de.turtle.services.CloudService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/files")
public class CloudController {

    private static final Logger logger = LoggerFactory.getLogger(CloudController.class);

    @Autowired
    private CloudService cloudService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("files") MultipartFile[] files, HttpServletRequest request) {
        Long ownerId = AuthController.getCurrentUserId(request);
        if(files.length == 1) {
            FileEntity savedFile = cloudService.storeFile(files[0], ownerId);
            if(savedFile == null) throw new IllegalArgumentException(); 
            return ResponseEntity.ok(List.of(savedFile));
        }else{
            List<FileEntity> savedFiles = cloudService.storeFiles(files, ownerId);
            if(savedFiles.isEmpty()) throw new IllegalArgumentException();
            return ResponseEntity.ok(savedFiles);
        }


        
    }

    @PostMapping("/encryption/{id}")
    public ResponseEntity<?> enDeCryptFile(@PathVariable Long id, @RequestBody String password, HttpServletRequest request){
        if(!cloudService.canUserModifyFile(id, AuthController.getCurrentUserId(request))){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid DeleteRequest: User doesnt own file.");
        }
        if(cloudService.enDeCryptFile(id, password)) {
            return ResponseEntity.ok("File encryption/decryption successful");
        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("File encryption/decryption not modified");

        
    }

    @PostMapping("/compression/{id}")
    public ResponseEntity<?> deCompressFile(@PathVariable Long id, HttpServletRequest request){
        if(!cloudService.canUserModifyFile(id, AuthController.getCurrentUserId(request))){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid DeleteRequest: User doesnt own file.");
        }
        if(cloudService.deCompressFile(id)){
            return ResponseEntity.ok("De/Compression Successful!");
        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("File encryption/decryption not modified");

    }
    

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id, HttpServletRequest request){
        if(!cloudService.canUserModifyFile(id, AuthController.getCurrentUserId(request))){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid DeleteRequest: User doesnt own file.");
        }

        if (cloudService.deleteFile(id) != null){
            return ResponseEntity.ok("Deletion successful!");
        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("File not deleted!");
        
    }

    @PostMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id, @RequestBody String password) {
        FileEntity entity = cloudService.getFileById(id);
        byte[] data = cloudService.downloadFile(id, password);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + entity.getName())
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(data);

    }

    @GetMapping("/list")
    public ResponseEntity<FileEntity[]> listFiles() {
        FileEntity[] files = cloudService.listFiles();
        ArrayList<FileEntity> freeFiles = new ArrayList<>();
        for(FileEntity f : files){
            if(f.getDir() == null){
                freeFiles.add(f);
            }
        }
        return ResponseEntity.ok(freeFiles.toArray(FileEntity[]::new));
    }

    @GetMapping("/getUsableSpace")
    public ResponseEntity<Long> usableSpace(){
        long spaceInBytes = cloudService.getAvailableSpace();
        logger.info("Fetched avaibile space");
        return ResponseEntity.ok(spaceInBytes);
    }

    @GetMapping("/getOccupiedSpace")
    public ResponseEntity<Long> occupiedSpace(){
        long spaceInBytes = cloudService.getOccupiedSpace();
        logger.info("Fetched occupied space");
        return ResponseEntity.ok(spaceInBytes);
    }
}
