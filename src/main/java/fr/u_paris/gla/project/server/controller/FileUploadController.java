package fr.u_paris.gla.project.server.controller;

import fr.u_paris.gla.project.server.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * REST controller for managing file uploads.
 *
 * @version 1.0
 * @author Tran Anh Duy NGUYEN
 */
@RestController
@RequestMapping("/files")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @Autowired
    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> handleCSVFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            String message = fileUploadService.uploadFile(file);
            return ResponseEntity.ok(message);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to upload file: " + e.getMessage());
        }
    }

}
