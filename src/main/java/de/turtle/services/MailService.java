package de.turtle.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import de.turtle.models.FileEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {
    
    @Autowired
    private JavaMailSender mailSender;

    private static final String senderEmail = "${MAIL_USERNAME}";

    @Value("${cloud.server.name:PiCloud}")
    private String serverName;

    @Value("${cloud.server.url:http://pi.local}")
    private String cloudUrl;

    public void sendDocument(FileEntity file, String recipientEmail) throws IOException, MessagingException {
        String htmlContent = buildHtml(recipientEmail, file.getName(), file.getSize());
        byte[] contents = Files.readAllBytes(Paths.get(file.getPath()));
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(senderEmail);
        helper.setTo(recipientEmail);
        helper.setSubject("You received a file from " + serverName);
        helper.setText(htmlContent, true);
        helper.addAttachment(file.getName(), new ByteArrayResource(contents));
        mailSender.send(message);
    }   

    private String buildHtml(String recipientEmail, String fileName, long fileSizeBytes) throws IOException {

        InputStream is = getClass().getResourceAsStream("/templates/email_send_file.html");
        if (is == null) {
            throw new IOException("Email template not found: /templates/email_send_file.html");
        }
        String template = StreamUtils.copyToString(is, StandardCharsets.UTF_8);

        String today    = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String fileType = fileName.contains(".")
                ? fileName.substring(fileName.lastIndexOf('.') + 1).toUpperCase()
                : "File";

        String displayName = recipientEmail.contains("@")
                ? recipientEmail.substring(0, recipientEmail.indexOf('@'))
                : recipientEmail;

        return template
                .replace("{{FILENAME}}",        fileName)
                .replace("{{FILESIZE}}",       Utils.formatFileSize(fileSizeBytes))
                .replace("{{DATE}}",             today)
                .replace("{{FILETYPE}}",          fileType)
                .replace("{{USERNAME}}",      displayName)
                .replace("{{SERVER_NAME}}",       serverName)
                .replace("{{RECIEVER_NAME}}",    displayName)
                .replace("{{RECIEVER_EMAIL}}",   recipientEmail)
                .replace("{{CLOUD_URL}}",         cloudUrl)
                .replace("{{YEAR}}",              String.valueOf(LocalDate.now().getYear()));
    }




}
