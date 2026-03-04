package de.turtle.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.turtle.models.FileEntity;
import de.turtle.services.CloudService;
import de.turtle.services.MailService;
import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api/mail")
public class MailController {

    @Autowired 
    private MailService mailService;

    @Autowired
    private CloudService cloudService;

    private record EmailRequest(String recipientEmail) {}
    
    @PostMapping("/file_request/{fileId}")
    public ResponseEntity<FileEntity> requestFile(@PathVariable Long fileId, @RequestBody EmailRequest emailRequest) {
        FileEntity file = cloudService.getFileById(fileId);
        try {
            mailService.sendDocument(file, emailRequest.recipientEmail);
        } catch (IOException e) {
           throw new RuntimeException("Failed to send email: Couldnt extract File");
        } catch(MessagingException e) {
            throw new RuntimeException("Failed to send email: Messaging error");
        }

        return ResponseEntity.ok(file);
    }

}
