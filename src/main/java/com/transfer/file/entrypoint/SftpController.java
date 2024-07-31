package com.transfer.file.entrypoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.transfer.file.server.SftpService;

import java.io.File;
import java.io.FileOutputStream;

@RestController
public class SftpController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SftpController.class);

    @Autowired
    private SftpService sftpService;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Convert MultipartFile to File
            File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();

            // Upload file to SFTP server
            sftpService.uploadFile(convFile.getPath(), file.getOriginalFilename());

            return "File uploaded successfully";
        } catch (Exception e) {
            // e.printStackTrace();
            LOGGER.error("upload error: {}", e.getMessage());
            return "File upload failed: " + e.getMessage();
        }
    }
}
