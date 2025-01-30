package com.ecommerce.javaecom.service;

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
        // file names of current/original file
        String originalFilename = file.getOriginalFilename(); // this will give us the total file name with extension

        // generate a unique file name
        String randomId = UUID.randomUUID().toString();
        String fileName = randomId.concat(originalFilename.substring(originalFilename.lastIndexOf(".")));
        String filePath = path + File.separator + fileName; // the new path to file -> path/fileName

        // check if paths exists, if not create
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdir(); // create a folder if it doesn't exist
        }

        // upload to server
        Files.copy(file.getInputStream(), Paths.get(filePath)); // copy the file in the filepath

        // return file name
        return fileName;
    }
}
