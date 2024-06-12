package fr.u_paris.gla.project.server.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Service for managing file uploads.
 *
 * @version 1.0
 * @author Tran Anh Duy NGUYEN
 */
@Service
public class FileUploadService {

    @Value("${upload.dir}")
    private String uploadDir;
    public String uploadFile(MultipartFile file) throws IOException {
        // create the directory if it doesn't exist
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new IOException("Failed to create upload directory.");
            }
        }
        // get original file name
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("No file selected.");
        }
        // save the file to a directory
        Path filePath = Paths.get(uploadDir, fileName);
        Files.write(filePath, file.getBytes());
        return "File uploaded successfully!";
    }

}
