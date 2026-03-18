package com.ecommerce.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        //file name of current file
        String originalFilename = file.getOriginalFilename();

        //Generate a unique file name
//        String randomId = UUID.randomUUID().toString();
        String fileName = UUID.randomUUID().toString() +
                originalFilename.substring(originalFilename.lastIndexOf("."));
        String filePath = path + File.separator + fileName;

        // check if path exist and create
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdir();
        }

        //upload to server
        Files.copy(file.getInputStream(), Paths.get(filePath));

        return fileName;
    }
}
