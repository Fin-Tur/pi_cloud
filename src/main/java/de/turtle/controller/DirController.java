package de.turtle.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.turtle.models.DirEntity;
import de.turtle.models.FileEntity;
import de.turtle.services.CloudService;
import de.turtle.services.DirService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/dirs")
public class DirController {

    private static final Logger logger = LoggerFactory.getLogger(DirController.class);

    //DTOs
    private record createDirRequest(String name, String password){}
    private record getFilesRequest(String password){}

    @Autowired
    private DirService dirService;

    @Autowired
    private CloudService cloudService;


    @GetMapping("/list")
    public ResponseEntity<DirEntity[]> listDirs(){
        DirEntity[] dirs = dirService.listDirs();
        return ResponseEntity.ok(dirs);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDir(@PathVariable Long id, HttpServletRequest request){
        if(!dirService.canUserModifyDir(id, AuthController.getCurrentUserId(request))){
            logger.warn("Unauthenticated Delete Request for dir: {}", id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid DeleteRequest: USer doesnt own Dir!");
        }

        if(dirService.deleteDir(id) != null){
            logger.info("Succesfully deleted Dir {}", id);
            return ResponseEntity.ok("Deletion Successful");
        }
        logger.error("An error occured while deleting dir {}", id);
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("Error deleting file");
    }

    @PostMapping("/create")
    public ResponseEntity<DirEntity> createDir(@RequestBody createDirRequest body, HttpServletRequest req){
        Long userid = AuthController.getCurrentUserId(req);
        logger.info("Got userID");
        DirEntity dir = dirService.createDir(body.name(), userid, body.password());
        logger.info("Created");
        return ResponseEntity.ok(dir);    
    }

    @PostMapping("/getFiles/{id}")
    public ResponseEntity<FileEntity[]> getFilesFromDir(@PathVariable Long id, @RequestBody getFilesRequest body, HttpServletRequest req){
        Long userID = AuthController.getCurrentUserId(req);
        if(!dirService.canUserModifyDir(id, userID)){
            logger.warn("User {} is not allowed to pull Files from Dir {}", userID, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        FileEntity[] files = dirService.getFilesFromDir(id, body.password());
        return ResponseEntity.ok(files);
    }

    @PostMapping("/getFilesByName/{name}")
    public ResponseEntity<FileEntity[]> getFilesFromDirByName(@PathVariable String name, @RequestBody String password, HttpServletRequest req){
        Long userID = AuthController.getCurrentUserId(req);
        DirEntity dir = dirService.getDirByName(name);
        if(dir == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(!dirService.canUserModifyDir(dir.getId(), userID)){
            logger.warn("User {} is not allowed to pull Files from Dir {}", userID, dir.getId());
            ResponseEntity.status(HttpStatus.FORBIDDEN);
        }

        FileEntity[] files = dirService.getFilesFromDir(dir.getId(), password);
        return ResponseEntity.ok(files);
    }

    @PostMapping("/move/{idF}/{idD}")
    public ResponseEntity<?> moveFileToDir(@PathVariable Long idF, @PathVariable Long idD, HttpServletRequest req){
        if(idD == 0){
            FileEntity file = cloudService.getFileById(idF);
            if(file.getDir() != null){
                DirEntity dir = dirService.getDirById(file.getDir().getId());
                if(dir == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                dir.removeFile(file);
                cloudService.saveFile(file);
                dirService.saveDir(dir);
            }
            return ResponseEntity.ok("File moved to home succesfully!");
        }

        Long userID = AuthController.getCurrentUserId(req);
        if(!(dirService.canUserModifyDir(idD, userID) && cloudService.canUserModifyFile(idF, userID))){
            logger.warn("User {} is not allowed to either acces file {} or dir {}", userID, idF, idD);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User does not have ownership required");
        }

        DirEntity dir = dirService.moveFileToDir(idF, idD);
        if(dir != null){
            logger.info("Moved file {} to dir {}", idF, idD);
            return ResponseEntity.ok("File moved successfully!");
        }
        logger.error("Error occured while moving file {} to dir {}", idF, idD);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occured while moving file.");
    }

    

}
